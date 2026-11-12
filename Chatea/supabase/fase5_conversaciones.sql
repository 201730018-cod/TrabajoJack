create function public.es_miembro(conversacion uuid)
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select exists (
    select 1
    from public.conversaciones_miembros
    where conversacion_id = conversacion
      and perfil_id = (select auth.uid())
  );
$$;

create policy "Ver mis conversaciones"
on public.conversaciones for select
to authenticated
using (public.es_miembro(id));

create policy "Ver participantes de mis conversaciones"
on public.conversaciones_miembros for select
to authenticated
using (public.es_miembro(conversacion_id));

create policy "Ver mensajes de mis conversaciones"
on public.mensajes for select
to authenticated
using (public.es_miembro(conversacion_id));

create function public.obtener_o_crear_conversacion(otro_usuario uuid)
returns uuid
language plpgsql
security definer
set search_path = ''
as $$
declare
  yo uuid := auth.uid();
  conversacion uuid;
begin
  if yo is null then
    raise exception 'Necesitas iniciar sesión';
  end if;
  if otro_usuario = yo then
    raise exception 'No puedes iniciar un chat contigo mismo';
  end if;
  if not exists (select 1 from public.perfiles where id = otro_usuario) then
    raise exception 'Ese usuario no existe';
  end if;

  perform pg_advisory_xact_lock(
    hashtextextended(least(yo, otro_usuario)::text || greatest(yo, otro_usuario)::text, 0)
  );

  select c.id into conversacion
  from public.conversaciones c
  join public.conversaciones_miembros m1 on m1.conversacion_id = c.id and m1.perfil_id = yo
  join public.conversaciones_miembros m2 on m2.conversacion_id = c.id and m2.perfil_id = otro_usuario
  where c.tipo = 'individual'
  limit 1;

  if conversacion is null then
    insert into public.conversaciones (tipo, creado_por)
    values ('individual', yo)
    returning id into conversacion;

    insert into public.conversaciones_miembros (conversacion_id, perfil_id)
    values (conversacion, yo), (conversacion, otro_usuario);
  end if;

  return conversacion;
end;
$$;

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
  ultimo_fecha timestamptz
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
    ultimo.creado_en
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
    select m.contenido, m.tipo, m.eliminado, m.remitente_id, m.creado_en
    from public.mensajes m
    where m.conversacion_id = c.id
    order by m.creado_en desc
    limit 1
  ) ultimo on true
  order by coalesce(ultimo.creado_en, c.actualizado_en) desc;
$$;

revoke execute on function public.es_miembro(uuid) from public, anon;
revoke execute on function public.obtener_o_crear_conversacion(uuid) from public, anon;
revoke execute on function public.mis_conversaciones() from public, anon;
grant execute on function public.es_miembro(uuid) to authenticated;
grant execute on function public.obtener_o_crear_conversacion(uuid) to authenticated;
grant execute on function public.mis_conversaciones() to authenticated;
