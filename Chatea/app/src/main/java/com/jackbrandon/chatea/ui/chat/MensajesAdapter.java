package com.jackbrandon.chatea.ui.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.model.Mensaje;
import com.jackbrandon.chatea.ui.Fechas;
import com.jackbrandon.chatea.ui.Imagenes;

public class MensajesAdapter extends ListAdapter<ElementoChat, RecyclerView.ViewHolder> {

    public interface Acciones {
        void reintentar(Mensaje mensaje);

        void opciones(Mensaje mensaje);

        void verImagen(Mensaje mensaje, String remitente);
    }

    private static final int[] COLORES_REMITENTE = {
            0xFF1FA855, 0xFF7F66FF, 0xFFE26A2C, 0xFF02A698, 0xFFD3396D, 0xFF3B82F6
    };

    private static final DiffUtil.ItemCallback<ElementoChat> DIFERENCIAS = new DiffUtil.ItemCallback<ElementoChat>() {
        @Override
        public boolean areItemsTheSame(@NonNull ElementoChat viejo, @NonNull ElementoChat nuevo) {
            return viejo.clave.equals(nuevo.clave);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ElementoChat viejo, @NonNull ElementoChat nuevo) {
            return viejo.mismoContenido(nuevo);
        }
    };

    private final Acciones acciones;

    public MensajesAdapter(Acciones acciones) {
        super(DIFERENCIAS);
        this.acciones = acciones;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).tipo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ElementoChat.FECHA) {
            return new FechaViewHolder(inflater.inflate(R.layout.item_fecha, parent, false));
        }
        int layout = viewType == ElementoChat.ENVIADO ? R.layout.item_mensaje_enviado : R.layout.item_mensaje_recibido;
        return new MensajeViewHolder(inflater.inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ElementoChat elemento = getItem(position);

        if (holder instanceof FechaViewHolder) {
            ((FechaViewHolder) holder).tvFecha.setText(elemento.etiquetaFecha);
            return;
        }

        MensajeViewHolder vh = (MensajeViewHolder) holder;
        Mensaje m = elemento.mensaje;
        Context context = vh.itemView.getContext();

        if (elemento.nombreRemitente != null) {
            vh.tvRemitente.setText(elemento.nombreRemitente);
            vh.tvRemitente.setTextColor(colorDe(m.remitenteId));
            vh.tvRemitente.setVisibility(View.VISIBLE);
        } else {
            vh.tvRemitente.setVisibility(View.GONE);
        }

        if (m.estaEliminado()) {
            vh.ivImagen.setVisibility(View.GONE);
            Glide.with(vh.ivImagen).clear(vh.ivImagen);
            vh.tvContenido.setVisibility(View.VISIBLE);
            vh.tvContenido.setText("🚫 Se eliminó este mensaje");
            vh.tvContenido.setTypeface(null, Typeface.ITALIC);
            vh.tvContenido.setTextColor(ContextCompat.getColor(context, R.color.texto_secundario));
        } else {
            mostrarImagen(vh, m, elemento.nombreRemitente);
            boolean hayTexto = m.contenido != null && !m.contenido.isEmpty();
            vh.tvContenido.setVisibility(hayTexto ? View.VISIBLE : View.GONE);
            vh.tvContenido.setText(m.contenido);
            vh.tvContenido.setTypeface(null, Typeface.NORMAL);
            vh.tvContenido.setTextColor(0xFF111B21);
        }
        vh.tvHora.setText(Fechas.hora(m.creadoEn));

        vh.burbuja.setOnLongClickListener(null);
        vh.burbuja.setLongClickable(false);
        if (vh.tvEstado != null) {
            switch (m.getEstado()) {
                case ENVIANDO:
                    vh.tvEstado.setText("🕓");
                    vh.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.texto_secundario));
                    vh.burbuja.setOnClickListener(null);
                    break;
                case ERROR:
                    vh.tvEstado.setText("⚠ Toca para reintentar");
                    vh.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.error));
                    vh.burbuja.setOnClickListener(v -> acciones.reintentar(m));
                    break;
                default:
                    boolean dobles = elemento.entrega != ElementoChat.Entrega.ENVIADO;
                    boolean leido = elemento.entrega == ElementoChat.Entrega.LEIDO;
                    vh.tvEstado.setText(m.estaEliminado() ? "" : dobles ? "✓✓" : "✓");
                    vh.tvEstado.setTextColor(ContextCompat.getColor(context,
                            leido ? R.color.palomita_leido : R.color.texto_secundario));
                    vh.burbuja.setOnClickListener(null);
                    if (!m.estaEliminado()) {
                        vh.burbuja.setOnLongClickListener(v -> {
                            acciones.opciones(m);
                            return true;
                        });
                    }
            }
        }
    }

    private void mostrarImagen(MensajeViewHolder vh, Mensaje m, String remitente) {
        if (!m.esImagen()) {
            vh.ivImagen.setVisibility(View.GONE);
            Glide.with(vh.ivImagen).clear(vh.ivImagen);
            return;
        }

        vh.ivImagen.setVisibility(View.VISIBLE);
        Object origen = m.uriLocal != null
                ? Uri.parse(m.uriLocal)
                : Imagenes.imagenPrivada(vh.itemView.getContext(), m.archivoUrl);
        Glide.with(vh.ivImagen)
                .load(origen)
                .placeholder(new android.graphics.drawable.ColorDrawable(Color.LTGRAY))
                .transform(new CenterCrop(), new RoundedCorners(24))
                .into(vh.ivImagen);
        vh.ivImagen.setOnClickListener(v -> acciones.verImagen(m, remitente));
    }

    private static int colorDe(String perfilId) {
        int indice = Math.abs(perfilId == null ? 0 : perfilId.hashCode()) % COLORES_REMITENTE.length;
        return COLORES_REMITENTE[indice];
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        final View burbuja;
        final TextView tvRemitente;
        final ImageView ivImagen;
        final TextView tvContenido;
        final TextView tvHora;
        final TextView tvEstado;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            burbuja = itemView.findViewById(R.id.burbuja);
            tvRemitente = itemView.findViewById(R.id.tvRemitente);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }

    static class FechaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFecha;

        FechaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
