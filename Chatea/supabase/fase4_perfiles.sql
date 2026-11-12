create policy "Usuarios con sesion pueden ver perfiles"
on public.perfiles for select
to authenticated
using (true);

create policy "Cada usuario edita solo su perfil"
on public.perfiles for update
to authenticated
using ((select auth.uid()) = id)
with check ((select auth.uid()) = id);

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('avatares', 'avatares', true, 5242880, array['image/jpeg', 'image/png', 'image/webp']);

create policy "Ver avatares"
on storage.objects for select
to authenticated
using (bucket_id = 'avatares');

create policy "Subir solo mi avatar"
on storage.objects for insert
to authenticated
with check (bucket_id = 'avatares' and (storage.foldername(name))[1] = (select auth.uid())::text);

create policy "Reemplazar solo mi avatar"
on storage.objects for update
to authenticated
using (bucket_id = 'avatares' and (storage.foldername(name))[1] = (select auth.uid())::text)
with check (bucket_id = 'avatares' and (storage.foldername(name))[1] = (select auth.uid())::text);

create policy "Borrar solo mi avatar"
on storage.objects for delete
to authenticated
using (bucket_id = 'avatares' and (storage.foldername(name))[1] = (select auth.uid())::text);
