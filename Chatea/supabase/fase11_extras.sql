-- 1. Fotografías de perfil
create function public.quitar_foto_perfil()
returns void
language sql
security definer
set search_path = ''
as $$
  update public.perfiles set foto_url = null where id = (select auth.uid());
$$;

-- 2. Envío de imágenes: bucket privado, cada carpeta es una conversación
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('chat-imagenes', 'chat-imagenes', false, 5242880, array['image/jpeg', 'image/png', 'image/webp']);

create policy "Ver imagenes de mis conversaciones"
on storage.objects for select
to authenticated
using (
  bucket_id = 'chat-imagenes'
  and exists (
    select 1 from public.conversaciones_miembros m
    where m.conversacion_id::text = (storage.foldername(name))[1]
      and m.perfil_id = (select auth.uid())
  )
);

create policy "Subir imagenes a mis conversaciones"
on storage.objects for insert
to authenticated
with check (
  bucket_id = 'chat-imagenes'
  and exists (
    select 1 from public.conversaciones_miembros m
    where m.conversacion_id::text = (storage.foldername(name))[1]
      and m.perfil_id = (select auth.uid())
  )
);

create policy "Borrar mis imagenes"
on storage.objects for delete
to authenticated
using (bucket_id = 'chat-imagenes' and owner_id = (select auth.uid())::text);

-- 3. Conversaciones grupales
create function public.crear_grupo(nombre_grupo text, foto text, miembros uuid[])
returns uuid
language plpgsql
security definer
set search_path = ''
as $$
declare
  yo uuid := auth.uid();
  grupo uuid;
  otros uuid[];
begin
  if yo is null then
    raise exception 'Necesitas iniciar sesión';
  end if;
  if coalesce(trim(nombre_grupo), '') = '' then
    raise exception 'El grupo necesita un nombre';
  end if;

  select array_agg(distinct p.id) into otros
  from public.perfiles p
  where p.id = any (miembros) and p.id <> yo;

  if otros is null or array_length(otros, 1) < 1 then
    raise exception 'Elige al menos a un participante';
  end if;

  insert into public.conversaciones (tipo, nombre, foto_url, creado_por)
  values ('grupo', trim(nombre_grupo), nullif(foto, ''), yo)
  returning id into grupo;

  insert into public.conversaciones_miembros (conversacion_id, perfil_id, rol)
  values (grupo, yo, 'admin');

  insert into public.conversaciones_miembros (conversacion_id, perfil_id, rol)
  select grupo, unnest(otros), 'miembro';

  return grupo;
end;
$$;

create function public.salir_de_grupo(conversacion uuid)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
  yo uuid := auth.uid();
  era_admin boolean;
begin
  delete from public.conversaciones_miembros m
  using public.conversaciones c
  where m.conversacion_id = conversacion
    and m.perfil_id = yo
    and c.id = m.conversacion_id
    and c.tipo = 'grupo'
  returning m.rol = 'admin' into era_admin;

  if era_admin is null then
    raise exception 'No perteneces a ese grupo';
  end if;

  if not exists (select 1 from public.conversaciones_miembros where conversacion_id = conversacion) then
    delete from public.conversaciones where id = conversacion;
  elsif era_admin and not exists (
    select 1 from public.conversaciones_miembros where conversacion_id = conversacion and rol = 'admin'
  ) then
    update public.conversaciones_miembros
    set rol = 'admin'
    where conversacion_id = conversacion
      and perfil_id = (
        select perfil_id from public.conversaciones_miembros
        where conversacion_id = conversacion
        order by unido_en
        limit 1
      );
  end if;
end;
$$;

drop function public.mis_conversaciones();

create function public.mis_conversaciones()
returns table (
  id uuid,
  tipo text,
  nombre text,
  foto_url text,
  otro_id uuid,
  actualizado_en timestamptz,
  ultimo_contenido text,
  ultimo_tipo text,
  ultimo_eliminado boolean,
  ultimo_es_mio boolean,
  ultimo_fecha timestamptz,
  otro_recibido_en timestamptz,
  otro_leido_en timestamptz,
  ultimo_remitente_nombre text
)
language sql
stable
set search_path = ''
as $$
  select
    c.id,
    c.tipo,
    coalesce(c.nombre, otro.nombre),
    coalesce(c.foto_url, otro.foto_url),
    otro.id,
    c.actualizado_en,
    ultimo.contenido,
    ultimo.tipo,
    ultimo.eliminado,
    ultimo.remitente_id = (select auth.uid()),
    ultimo.creado_en,
    lecturas.recibido_en,
    lecturas.leido_en,
    remitente.nombre
  from public.conversaciones c
  join public.conversaciones_miembros yo
    on yo.conversacion_id = c.id and yo.perfil_id = (select auth.uid())
  left join lateral (
    select p.id, p.nombre, p.foto_url
    from public.conversaciones_miembros m
    join public.perfiles p on p.id = m.perfil_id
    where m.conversacion_id = c.id and m.perfil_id <> (select auth.uid())
    limit 1
  ) otro on c.tipo = 'individual'
  left join lateral (
    select
      case when count(*) = count(m.ultimo_recibido_en) then min(m.ultimo_recibido_en) end as recibido_en,
      case when count(*) = count(m.ultimo_leido_en) then min(m.ultimo_leido_en) end as leido_en
    from public.conversaciones_miembros m
    where m.conversacion_id = c.id and m.perfil_id <> (select auth.uid())
  ) lecturas on true
  left join lateral (
    select m.contenido, m.tipo, m.eliminado, m.remitente_id, m.creado_en
    from public.mensajes m
    where m.conversacion_id = c.id
    order by m.creado_en desc
    limit 1
  ) ultimo on true
  left join public.perfiles remitente on remitente.id = ultimo.remitente_id
  order by coalesce(ultimo.creado_en, c.actualizado_en) desc;
$$;

-- 4. Eliminación de mensajes (para todos)
create function public.eliminar_mensaje(mensaje uuid)
returns void
language plpgsql
security definer
set search_path = ''
as $$
begin
  update public.mensajes
  set eliminado = true,
      contenido = null,
      archivo_url = null
  where id = mensaje
    and remitente_id = (select auth.uid())
    and eliminado = false;

  if not found then
    raise exception 'Solo puedes eliminar tus propios mensajes';
  end if;
end;
$$;

-- 5. Búsqueda de mensajes en todos mis chats
create extension if not exists pg_trgm with schema extensions;

create index mensajes_contenido_trgm_idx
on public.mensajes using gin (contenido extensions.gin_trgm_ops);

create function public.buscar_mensajes(texto text)
returns table (
  id uuid,
  conversacion_id uuid,
  contenido text,
  creado_en timestamptz,
  es_mio boolean,
  remitente_nombre text,
  chat_tipo text,
  chat_nombre text,
  chat_foto_url text,
  otro_id uuid
)
language sql
stable
set search_path = ''
as $$
  select
    m.id,
    m.conversacion_id,
    m.contenido,
    m.creado_en,
    m.remitente_id = (select auth.uid()),
    remitente.nombre,
    c.tipo,
    coalesce(c.nombre, otro.nombre),
    coalesce(c.foto_url, otro.foto_url),
    otro.id
  from public.mensajes m
  join public.conversaciones c on c.id = m.conversacion_id
  left join public.perfiles remitente on remitente.id = m.remitente_id
  left join lateral (
    select p.id, p.nombre, p.foto_url
    from public.conversaciones_miembros cm
    join public.perfiles p on p.id = cm.perfil_id
    where cm.conversacion_id = c.id and cm.perfil_id <> (select auth.uid())
    limit 1
  ) otro on c.tipo = 'individual'
  where m.eliminado = false
    and length(trim(texto)) >= 2
    and m.contenido ilike '%' || replace(replace(replace(trim(texto), '\', '\\'), '%', '\%'), '_', '\_') || '%'
  order by m.creado_en desc
  limit 50;
$$;

revoke execute on function public.quitar_foto_perfil() from public, anon;
revoke execute on function public.crear_grupo(text, text, uuid[]) from public, anon;
revoke execute on function public.salir_de_grupo(uuid) from public, anon;
revoke execute on function public.mis_conversaciones() from public, anon;
revoke execute on function public.eliminar_mensaje(uuid) from public, anon;
revoke execute on function public.buscar_mensajes(text) from public, anon;
grant execute on function public.quitar_foto_perfil() to authenticated;
grant execute on function public.crear_grupo(text, text, uuid[]) to authenticated;
grant execute on function public.salir_de_grupo(uuid) to authenticated;
grant execute on function public.mis_conversaciones() to authenticated;
grant execute on function public.eliminar_mensaje(uuid) to authenticated;
grant execute on function public.buscar_mensajes(text) to authenticated;
