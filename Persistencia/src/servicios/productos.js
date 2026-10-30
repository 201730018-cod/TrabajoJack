import { supabase } from '../lib/supabase';

const TABLA = 'productos';

function revisar({ data, error }, accion) {
  if (error) throw new Error(error.message);
  if (Array.isArray(data) && data.length === 0) {
    throw new Error(`No se pudo ${accion}. Revisa las políticas (RLS) de la tabla en Supabase.`);
  }
  return data;
}

export async function obtenerProductos() {
  const respuesta = await supabase
    .from(TABLA)
    .select('id, producto, descripcion, precio, created_at')
    .order('created_at', { ascending: false });
  if (respuesta.error) throw new Error(respuesta.error.message);
  return respuesta.data;
}

export async function crearProducto(datos) {
  const respuesta = await supabase.from(TABLA).insert(datos).select();
  return revisar(respuesta, 'guardar')[0];
}

export async function actualizarProducto(id, datos) {
  const respuesta = await supabase.from(TABLA).update(datos).eq('id', id).select();
  return revisar(respuesta, 'actualizar')[0];
}

export async function eliminarProducto(id) {
  const respuesta = await supabase.from(TABLA).delete().eq('id', id).select();
  revisar(respuesta, 'eliminar');
}
