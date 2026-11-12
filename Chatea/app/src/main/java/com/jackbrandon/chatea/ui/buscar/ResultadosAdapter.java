package com.jackbrandon.chatea.ui.buscar;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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

import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.model.ResultadoBusqueda;
import com.jackbrandon.chatea.ui.Fechas;
import com.jackbrandon.chatea.ui.Imagenes;

import java.util.Locale;

public class ResultadosAdapter extends ListAdapter<ResultadoBusqueda, ResultadosAdapter.ViewHolder> {

    public interface AlAbrir {
        void resultado(ResultadoBusqueda resultado);
    }

    private static final DiffUtil.ItemCallback<ResultadoBusqueda> DIFERENCIAS = new DiffUtil.ItemCallback<ResultadoBusqueda>() {
        @Override
        public boolean areItemsTheSame(@NonNull ResultadoBusqueda viejo, @NonNull ResultadoBusqueda nuevo) {
            return viejo.id.equals(nuevo.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ResultadoBusqueda viejo, @NonNull ResultadoBusqueda nuevo) {
            return viejo.equals(nuevo);
        }
    };

    private final AlAbrir alAbrir;
    private String buscado = "";

    public ResultadosAdapter(AlAbrir alAbrir) {
        super(DIFERENCIAS);
        this.alAbrir = alAbrir;
    }

    public void mostrar(java.util.List<ResultadoBusqueda> lista, String texto) {
        buscado = texto;
        submitList(null);
        submitList(lista);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultadoBusqueda r = getItem(position);
        holder.tvNombre.setText(r.chatNombre == null ? "Chat" : r.chatNombre);
        holder.tvHora.setText(Fechas.paraListaDeChats(r.creadoEn));
        Imagenes.cargarAvatar(holder.ivAvatar, r.chatFotoUrl);

        String prefijo = Boolean.TRUE.equals(r.esMio) ? "Tú: "
                : "grupo".equals(r.chatTipo) && r.remitenteNombre != null ? r.remitenteNombre + ": " : "";
        String texto = prefijo + (r.contenido == null ? "" : r.contenido);

        SpannableString resaltado = new SpannableString(texto);
        int inicio = texto.toLowerCase(Locale.getDefault()).indexOf(buscado.toLowerCase(Locale.getDefault()));
        if (inicio >= 0 && !buscado.isEmpty()) {
            int fin = inicio + buscado.length();
            resaltado.setSpan(new StyleSpan(Typeface.BOLD), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            resaltado.setSpan(new ForegroundColorSpan(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.verde_oscuro)),
                    inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.tvUltimoMensaje.setText(resaltado);
        holder.tvUltimoMensaje.setMaxLines(2);

        holder.itemView.setOnClickListener(v -> alAbrir.resultado(r));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final TextView tvNombre;
        final TextView tvHora;
        final TextView tvUltimoMensaje;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUltimoMensaje = itemView.findViewById(R.id.tvUltimoMensaje);
        }
    }
}
