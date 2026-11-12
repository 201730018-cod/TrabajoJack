alter table public.conversaciones_miembros
add column ultimo_recibido_en timestamptz;

create function public.marcar_recibidos()
returns void
language sql
security definer
set search_path = ''
as $$
  update public.conversaciones_miembros m
  set ultimo_recibido_en = now()
  where m.perfil_id = (select auth.uid())
    and exists (
      select 1
      from public.mensajes x
      where x.conversacion_id = m.conversacion_id
        and x.remitente_id <> m.perfil_id
        and x.creado_en > coalesce(m.ultimo_recibido_en, '-infinity')
    );
$$;

create function public.marcar_leido(conversacion uuid)
returns void
language sql
security definer
set search_path = ''
as $$
  update public.conversaciones_miembros m
  set ultimo_leido_en = now(),
      ultimo_recibido_en = now()
  where m.conversacion_id = conversacion
    and m.perfil_id = (select auth.uid())
    and exists (
      select 1
      from public.mensajes x
      where x.conversacion_id = m.conversacion_id
        and x.remitente_id <> m.perfil_id
        and x.creado_en > coalesce(m.ultimo_leido_en, '-infinity')
    );
$$;

create function public.registrar_conexion()
returns void
language sql
security definer
set search_path = ''
as $$
  update public.perfiles
  set ultima_conexion = now()
  where id = (select auth.uid());
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
  otro_leido_en timestamptz
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
    otro.ultimo_recibido_en,
    otro.ultimo_leido_en
  from public.conversaciones c
  join public.conversaciones_miembros yo
    on yo.conversacion_id = c.id and yo.perfil_id = (select auth.uid())
  left join lateral (
    select p.id, p.nombre, p.foto_url, m.ultimo_recibido_en, m.ultimo_leido_en
    from public.conversaciones_miembros m
    join public.perfiles p on p.id = m.perfil_id
    where m.conversacion_id = c.id and m.perfil_id <> (select auth.uid())
    limit 1
  ) otro on c.tipo = 'individual'
  left join lateral (
    select m.contenido, m.tipo, m.eliminado, m.remitente_id, m.creado_en
    from public.mensajes m
    where m.conversacion_id = c.id
    order by m.creado_en desc
    limit 1
  ) ultimo on true
  order by coalesce(ultimo.creado_en, c.actualizado_en) desc;
$$;

revoke execute on function public.marcar_recibidos() from public, anon;
revoke execute on function public.marcar_leido(uuid) from public, anon;
revoke execute on function public.registrar_conexion() from public, anon;
revoke execute on function public.mis_conversaciones() from public, anon;
grant execute on function public.marcar_recibidos() to authenticated;
grant execute on function public.marcar_leido(uuid) to authenticated;
grant execute on function public.registrar_conexion() to authenticated;
grant execute on function public.mis_conversaciones() to authenticated;

alter publication supabase_realtime add table public.conversaciones_miembros, public.perfiles;
