create function public.registrar_dispositivo(token text, modelo_celular text)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
  yo uuid := auth.uid();
begin
  if yo is null then
    raise exception 'Necesitas iniciar sesión';
  end if;

  delete from public.dispositivos
  where token_push = token and perfil_id <> yo;

  insert into public.dispositivos (perfil_id, token_push, plataforma, modelo)
  values (yo, token, 'android', modelo_celular)
  on conflict (token_push) do update
  set ultima_actividad = now(),
      modelo = excluded.modelo;
end;
$$;

create function public.borrar_dispositivo(token text)
returns void
language sql
security definer
set search_path = ''
as $$
  delete from public.dispositivos
  where token_push = token and perfil_id = (select auth.uid());
$$;

create policy "Ver mis dispositivos"
on public.dispositivos for select
to authenticated
using (perfil_id = (select auth.uid()));

revoke execute on function public.registrar_dispositivo(text, text) from public, anon;
revoke execute on function public.borrar_dispositivo(text) from public, anon;
grant execute on function public.registrar_dispositivo(text, text) to authenticated;
grant execute on function public.borrar_dispositivo(text) to authenticated;
