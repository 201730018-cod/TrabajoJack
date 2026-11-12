import { createClient } from "npm:@supabase/supabase-js@2";

const SUPABASE_URL = Deno.env.get("SUPABASE_URL")!;
const SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
const SECRETO_TRIGGER = (Deno.env.get("CHATEA_WEBHOOK_SECRET") ?? "").trim().replace(/^"|"$/g, "");
const CUENTA_SERVICIO = JSON.parse(Deno.env.get("FCM_SERVICE_ACCOUNT") ?? "{}");

const supabase = createClient(SUPABASE_URL, SERVICE_ROLE_KEY, {
  auth: { persistSession: false },
});

const TEXTO_POR_TIPO: Record<string, string> = {
  imagen: "📷 Foto",
  audio: "🎤 Audio",
  archivo: "📎 Archivo",
};

let tokenGoogle: { valor: string; expira: number } | null = null;

Deno.serve(async (peticion) => {
  if (peticion.method !== "POST") {
    return new Response("Método no permitido", { status: 405 });
  }

  const secreto = peticion.headers.get("x-chatea-secreto") ?? "";
  if (!SECRETO_TRIGGER || !mismoTexto(secreto, SECRETO_TRIGGER)) {
    console.warn(
      !SECRETO_TRIGGER
        ? "Falta el secreto CHATEA_WEBHOOK_SECRET en la Edge Function"
        : `La clave no coincide (llegó una de ${secreto.length} caracteres, se esperaba una de ${SECRETO_TRIGGER.length})`,
    );
    return new Response("No autorizado", { status: 401 });
  }

  const { mensaje_id } = await peticion.json();
  if (!mensaje_id) {
    return new Response("Falta mensaje_id", { status: 400 });
  }

  const { data: mensaje, error: errorMensaje } = await supabase
    .from("mensajes")
    .select("id, conversacion_id, remitente_id, tipo, contenido, eliminado")
    .eq("id", mensaje_id)
    .single();
  if (errorMensaje || !mensaje || mensaje.eliminado || !mensaje.remitente_id) {
    return Response.json({ enviados: 0, motivo: "mensaje no encontrado" });
  }

  const { data: remitente } = await supabase
    .from("perfiles")
    .select("nombre, foto_url")
    .eq("id", mensaje.remitente_id)
    .single();

  const { data: conversacion } = await supabase
    .from("conversaciones")
    .select("tipo, nombre, foto_url")
    .eq("id", mensaje.conversacion_id)
    .single();
  const esGrupo = conversacion?.tipo === "grupo";

  const { data: miembros } = await supabase
    .from("conversaciones_miembros")
    .select("perfil_id, silenciado")
    .eq("conversacion_id", mensaje.conversacion_id)
    .neq("perfil_id", mensaje.remitente_id);

  const destinatarios = (miembros ?? [])
    .filter((m) => !m.silenciado)
    .map((m) => m.perfil_id);
  if (destinatarios.length === 0) {
    return Response.json({ enviados: 0, motivo: "sin destinatarios" });
  }

  const { data: dispositivos } = await supabase
    .from("dispositivos")
    .select("id, token_push")
    .in("perfil_id", destinatarios)
    .not("token_push", "is", null);
  if (!dispositivos || dispositivos.length === 0) {
    return Response.json({ enviados: 0, motivo: "sin dispositivos registrados" });
  }

  const contenido = mensaje.tipo === "texto"
    ? recortar(mensaje.contenido ?? "", 200)
    : TEXTO_POR_TIPO[mensaje.tipo] ?? "Mensaje nuevo";
  const nombreRemitente = remitente?.nombre ?? "Alguien";

  const datos = {
    tipo: "mensaje",
    conversacion_id: mensaje.conversacion_id,
    remitente_id: mensaje.remitente_id,
    tipo_chat: esGrupo ? "grupo" : "individual",
    titulo: esGrupo ? conversacion?.nombre ?? "Grupo" : nombreRemitente,
    texto: esGrupo ? `${nombreRemitente}: ${contenido}` : contenido,
    chat_nombre: esGrupo ? conversacion?.nombre ?? "Grupo" : nombreRemitente,
    chat_foto: (esGrupo ? conversacion?.foto_url : remitente?.foto_url) ?? "",
    foto_url: remitente?.foto_url ?? "",
  };

  const accessToken = await obtenerTokenDeGoogle();
  let enviados = 0;
  const tokensInvalidos: string[] = [];

  for (const dispositivo of dispositivos) {
    const respuesta = await fetch(
      `https://fcm.googleapis.com/v1/projects/${CUENTA_SERVICIO.project_id}/messages:send`,
      {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          message: {
            token: dispositivo.token_push,
            data: datos,
            android: { priority: "HIGH" },
          },
        }),
      },
    );

    if (respuesta.ok) {
      enviados++;
    } else {
      const error = await respuesta.text();
      console.error("FCM rechazó un envío:", respuesta.status, error);
      if (respuesta.status === 404 || error.includes("UNREGISTERED")) {
        tokensInvalidos.push(dispositivo.id);
      }
    }
  }

  if (tokensInvalidos.length > 0) {
    await supabase.from("dispositivos").delete().in("id", tokensInvalidos);
  }

  return Response.json({ enviados, tokens_borrados: tokensInvalidos.length });
});

async function obtenerTokenDeGoogle(): Promise<string> {
  if (tokenGoogle && tokenGoogle.expira > Date.now() + 60_000) {
    return tokenGoogle.valor;
  }

  const ahora = Math.floor(Date.now() / 1000);
  const cabecera = { alg: "RS256", typ: "JWT" };
  const contenido = {
    iss: CUENTA_SERVICIO.client_email,
    scope: "https://www.googleapis.com/auth/firebase.messaging",
    aud: "https://oauth2.googleapis.com/token",
    iat: ahora,
    exp: ahora + 3600,
  };

  const sinFirmar = `${base64Url(JSON.stringify(cabecera))}.${base64Url(JSON.stringify(contenido))}`;
  const llave = await crypto.subtle.importKey(
    "pkcs8",
    pemABytes(CUENTA_SERVICIO.private_key),
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const firma = await crypto.subtle.sign(
    "RSASSA-PKCS1-v1_5",
    llave,
    new TextEncoder().encode(sinFirmar),
  );
  const jwt = `${sinFirmar}.${base64Url(new Uint8Array(firma))}`;

  const respuesta = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion: jwt,
    }),
  });
  if (!respuesta.ok) {
    throw new Error(`Google rechazó la cuenta de servicio: ${await respuesta.text()}`);
  }

  const { access_token, expires_in } = await respuesta.json();
  tokenGoogle = { valor: access_token, expira: Date.now() + expires_in * 1000 };
  return access_token;
}

function base64Url(datos: string | Uint8Array): string {
  const bytes = typeof datos === "string" ? new TextEncoder().encode(datos) : datos;
  let binario = "";
  for (const b of bytes) binario += String.fromCharCode(b);
  return btoa(binario).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

function pemABytes(pem: string): ArrayBuffer {
  const base64 = pem
    .replace(/-----BEGIN PRIVATE KEY-----/, "")
    .replace(/-----END PRIVATE KEY-----/, "")
    .replace(/\s+/g, "");
  return Uint8Array.from(atob(base64), (c) => c.charCodeAt(0)).buffer;
}

function mismoTexto(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diferencia = 0;
  for (let i = 0; i < a.length; i++) diferencia |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diferencia === 0;
}

function recortar(texto: string, maximo: number): string {
  return texto.length > maximo ? texto.slice(0, maximo - 1) + "…" : texto;
}
