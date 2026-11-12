create extension if not exists pg_net with schema extensions;

select vault.create_secret(
  encode(extensions.gen_random_bytes(32), 'hex'),
  'chatea_webhook_secreto',
  'Clave que comparte el trigger de mensajes con la Edge Function notificar-mensaje'
);

select vault.create_secret(
  'https://TU-PROYECTO.supabase.co/functions/v1/notificar-mensaje',
  'chatea_url_notificar',
  'URL de la Edge Function notificar-mensaje'
);

create function public.avisar_mensaje_nuevo()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
  url text;
  secreto text;
begin
  select decrypted_secret into url
  from vault.decrypted_secrets
  where name = 'chatea_url_notificar';

  select decrypted_secret into secreto
  from vault.decrypted_secrets
  where name = 'chatea_webhook_secreto';

  if url is null or secreto is null then
    return new;
  end if;

  perform net.http_post(
    url := url,
    headers := jsonb_build_object(
      'Content-Type', 'application/json',
      'x-chatea-secreto', secreto
    ),
    body := jsonb_build_object('mensaje_id', new.id)
  );

  return new;
end;
$$;

revoke execute on function public.avisar_mensaje_nuevo() from public, anon, authenticated;

create trigger al_insertar_mensaje_notificar
after insert on public.mensajes
for each row execute function public.avisar_mensaje_nuevo();
