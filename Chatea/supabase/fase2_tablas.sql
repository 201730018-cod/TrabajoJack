-- 1. perfiles
create table public.perfiles (
  id uuid primary key references auth.users (id) on delete cascade,
  nombre text not null,
  telefono text unique,
  foto_url text,
  info text not null default '¡Hola! Estoy usando Chatea',
  ultima_conexion timestamptz,
  creado_en timestamptz not null default now()
);

-- 2. dispositivos
create table public.dispositivos (
  id uuid primary key default gen_random_uuid(),
  perfil_id uuid not null references public.perfiles (id) on delete cascade,
  token_push text unique,
  plataforma text not null default 'android' check (plataforma in ('android', 'ios', 'web')),
  modelo text,
  ultima_actividad timestamptz not null default now(),
  creado_en timestamptz not null default now()
);

create index dispositivos_perfil_idx on public.dispositivos (perfil_id);

-- 3. conversaciones
create table public.conversaciones (
  id uuid primary key default gen_random_uuid(),
  tipo text not null default 'individual' check (tipo in ('individual', 'grupo')),
  nombre text,
  foto_url text,
  creado_por uuid references public.perfiles (id) on delete set null,
  actualizado_en timestamptz not null default now(),
  creado_en timestamptz not null default now(),
  constraint grupo_con_nombre check (tipo = 'individual' or nombre is not null)
);

-- 4. conversaciones_miembros
create table public.conversaciones_miembros (
  conversacion_id uuid not null references public.conversaciones (id) on delete cascade,
  perfil_id uuid not null references public.perfiles (id) on delete cascade,
  rol text not null default 'miembro' check (rol in ('admin', 'miembro')),
  ultimo_leido_en timestamptz,
  silenciado boolean not null default false,
  unido_en timestamptz not null default now(),
  primary key (conversacion_id, perfil_id)
);

create index conversaciones_miembros_perfil_idx on public.conversaciones_miembros (perfil_id);

-- 5. mensajes
create table public.mensajes (
  id uuid primary key default gen_random_uuid(),
  conversacion_id uuid not null references public.conversaciones (id) on delete cascade,
  remitente_id uuid references public.perfiles (id) on delete set null,
  tipo text not null default 'texto' check (tipo in ('texto', 'imagen', 'audio', 'archivo')),
  contenido text,
  archivo_url text,
  responde_a uuid references public.mensajes (id) on delete set null,
  eliminado boolean not null default false,
  editado_en timestamptz,
  creado_en timestamptz not null default now(),
  constraint mensaje_con_contenido check (
    eliminado
    or (tipo = 'texto' and coalesce(trim(contenido), '') <> '')
    or (tipo <> 'texto' and archivo_url is not null)
  )
);

create index mensajes_conversacion_fecha_idx on public.mensajes (conversacion_id, creado_en desc);

-- 6. actualizar la fecha del chat con cada mensaje nuevo
create function public.actualizar_fecha_conversacion()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  update public.conversaciones
  set actualizado_en = new.creado_en
  where id = new.conversacion_id;
  return new;
end;
$$;

create trigger al_insertar_mensaje
after insert on public.mensajes
for each row execute function public.actualizar_fecha_conversacion();

-- 7. seguridad: nadie puede leer ni escribir hasta que agreguemos reglas en la fase 3
alter table public.perfiles enable row level security;
alter table public.dispositivos enable row level security;
alter table public.conversaciones enable row level security;
alter table public.conversaciones_miembros enable row level security;
alter table public.mensajes enable row level security;
