create table if not exists public.productos (
  id bigint generated always as identity primary key,
  producto text not null,
  descripcion text default '',
  precio numeric(10, 2) not null check (precio >= 0),
  created_at timestamptz not null default now()
);

alter table public.productos enable row level security;

create policy "Leer productos" on public.productos
  for select using (true);

create policy "Agregar productos" on public.productos
  for insert with check (true);

create policy "Editar productos" on public.productos
  for update using (true) with check (true);

create policy "Eliminar productos" on public.productos
  for delete using (true);
