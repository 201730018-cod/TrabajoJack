create policy "Enviar mensajes a mis conversaciones"
on public.mensajes for insert
to authenticated
with check (
  remitente_id = (select auth.uid())
  and public.es_miembro(conversacion_id)
  and eliminado = false
);

alter table public.mensajes
add constraint mensaje_largo_maximo check (char_length(contenido) <= 4000);
