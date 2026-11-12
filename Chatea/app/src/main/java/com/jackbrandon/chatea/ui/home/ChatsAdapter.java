package com.jackbrandon.chatea.ui.home;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.jackbrandon.chatea.data.model.ChatResumen;
import com.jackbrandon.chatea.ui.Fechas;
import com.jackbrandon.chatea.ui.Imagenes;
import com.jackbrandon.chatea.ui.VerFotoActivity;

public class ChatsAdapter extends ListAdapter<ChatResumen, ChatsAdapter.ChatViewHolder> {

    public interface AlAbrir {
        void chat(ChatResumen chat);
    }

    private static final DiffUtil.ItemCallback<ChatResumen> DIFERENCIAS = new DiffUtil.ItemCallback<ChatResumen>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatResumen viejo, @NonNull ChatResumen nuevo) {
            return viejo.id.equals(nuevo.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatResumen viejo, @NonNull ChatResumen nuevo) {
            return viejo.equals(nuevo);
        }
    };

    private final AlAbrir alAbrir;

    public ChatsAdapter(AlAbrir alAbrir) {
        super(DIFERENCIAS);
        this.alAbrir = alAbrir;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatResumen chat = getItem(position);
        holder.tvNombre.setText(chat.nombre == null ? "Usuario de Chatea" : chat.nombre);
        Imagenes.cargarAvatar(holder.ivAvatar, chat.fotoUrl);
        holder.ivAvatar.setOnClickListener(v ->
                VerFotoActivity.abrirPublica(v.getContext(), chat.fotoUrl, chat.nombre));

        holder.tvHora.setText(Fechas.paraListaDeChats(
                chat.tieneMensajes() ? chat.ultimoFecha : chat.actualizadoEn));
        Context context = holder.itemView.getContext();
        if (chat.escribiendo) {
            holder.tvUltimoMensaje.setText("escribiendo…");
            holder.tvUltimoMensaje.setTextColor(ContextCompat.getColor(context, R.color.verde));
            holder.tvUltimoMensaje.setTypeface(null, Typeface.BOLD);
        } else {
            holder.tvUltimoMensaje.setText(conPalomitas(context, chat));
            holder.tvUltimoMensaje.setTextColor(ContextCompat.getColor(context, R.color.texto_secundario));
            holder.tvUltimoMensaje.setTypeface(null, chat.tieneMensajes() ? Typeface.NORMAL : Typeface.ITALIC);
        }

        holder.itemView.setOnClickListener(v -> alAbrir.chat(chat));
    }

    private static CharSequence conPalomitas(Context context, ChatResumen chat) {
        String texto = textoUltimoMensaje(chat);
        if (!chat.tieneMensajes() || !Boolean.TRUE.equals(chat.ultimoEsMio)) return texto;

        boolean leido = Fechas.noAntesDe(chat.otroLeidoEn, chat.ultimoFecha);
        boolean recibido = leido || Fechas.noAntesDe(chat.otroRecibidoEn, chat.ultimoFecha);
        String palomitas = recibido ? "✓✓ " : "✓ ";

        SpannableString resultado = new SpannableString(palomitas + texto);
        if (leido) {
            resultado.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.palomita_leido)),
                    0, palomitas.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return resultado;
    }

    static String textoUltimoMensaje(ChatResumen chat) {
        if (!chat.tieneMensajes()) return "Sin mensajes todavía";

        String prefijo = "";
        if (Boolean.TRUE.equals(chat.ultimoEsMio)) {
            prefijo = "Tú: ";
        } else if (chat.esGrupo() && chat.ultimoRemitenteNombre != null) {
            prefijo = chat.ultimoRemitenteNombre + ": ";
        }
        if (Boolean.TRUE.equals(chat.ultimoEliminado)) return prefijo + "🚫 Se eliminó este mensaje";
        if ("imagen".equals(chat.ultimoTipo)) return prefijo + "📷 Foto";
        if ("audio".equals(chat.ultimoTipo)) return prefijo + "🎤 Audio";
        if ("archivo".equals(chat.ultimoTipo)) return prefijo + "📎 Archivo";
        return prefijo + chat.ultimoContenido;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final TextView tvNombre;
        final TextView tvHora;
        final TextView tvUltimoMensaje;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUltimoMensaje = itemView.findViewById(R.id.tvUltimoMensaje);
        }
    }
}
