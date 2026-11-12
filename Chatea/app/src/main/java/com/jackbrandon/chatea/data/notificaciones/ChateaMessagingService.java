package com.jackbrandon.chatea.data.notificaciones;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jackbrandon.chatea.ui.chat.ChatActivity;

import java.util.Map;

public class ChateaMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Firebase generó un token nuevo");
        new TokenRepository(this).registrar(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensaje) {
        Map<String, String> datos = mensaje.getData();

        if ("mensaje".equals(datos.get("tipo"))) {
            mostrarMensajeDeChat(datos);
            return;
        }

        RemoteMessage.Notification notificacion = mensaje.getNotification();
        String titulo = notificacion != null && notificacion.getTitle() != null
                ? notificacion.getTitle() : datos.get("titulo");
        String texto = notificacion != null && notificacion.getBody() != null
                ? notificacion.getBody() : datos.get("texto");

        String canal = datos.get("canal");
        if (canal == null && notificacion != null) canal = notificacion.getChannelId();
        if (!Notificaciones.CANAL_GENERAL.equals(canal)) canal = Notificaciones.CANAL_MENSAJES;

        Log.d(TAG, "Notificación recibida: " + titulo);
        if (titulo == null && texto == null) return;

        Notificaciones.mostrar(this, canal, titulo == null ? "Chatea" : titulo, texto == null ? "" : texto);
    }

    private void mostrarMensajeDeChat(Map<String, String> datos) {
        String conversacionId = datos.get("conversacion_id");
        if (conversacionId == null) return;

        if (conversacionId.equals(ChatActivity.conversacionVisible)) {
            Log.d(TAG, "El chat ya está abierto, no se muestra la notificación");
            return;
        }

        String tipoChat = valorO(datos.get("tipo_chat"), "individual");
        String otroId = "grupo".equals(tipoChat) ? null : datos.get("remitente_id");
        String nombreChat = valorO(datos.get("chat_nombre"), datos.get("titulo"));
        String fotoChat = valorO(datos.get("chat_foto"), datos.get("foto_url"));

        Log.d(TAG, "Mensaje nuevo en " + nombreChat);
        Notificaciones.mostrarMensaje(
                this,
                conversacionId,
                tipoChat,
                otroId,
                nombreChat,
                fotoChat,
                datos.get("titulo"),
                datos.get("texto"));
    }

    private static String valorO(String valor, String porDefecto) {
        return valor == null || valor.isEmpty() ? porDefecto : valor;
    }
}
