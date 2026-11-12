package com.jackbrandon.chatea.ui.contactos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.ui.Imagenes;
import com.jackbrandon.chatea.ui.VerFotoActivity;

public class ContactosAdapter extends ListAdapter<Perfil, ContactosAdapter.ContactoViewHolder> {

    public interface AlSeleccionar {
        void contacto(Perfil perfil);
    }

    private static final DiffUtil.ItemCallback<Perfil> DIFERENCIAS = new DiffUtil.ItemCallback<Perfil>() {
        @Override
        public boolean areItemsTheSame(@NonNull Perfil viejo, @NonNull Perfil nuevo) {
            return viejo.id.equals(nuevo.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Perfil viejo, @NonNull Perfil nuevo) {
            return viejo.equals(nuevo);
        }
    };

    private final AlSeleccionar alSeleccionar;

    public ContactosAdapter(AlSeleccionar alSeleccionar) {
        super(DIFERENCIAS);
        this.alSeleccionar = alSeleccionar;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Perfil perfil = getItem(position);
        holder.tvNombre.setText(perfil.nombre);
        holder.tvInfo.setText(perfil.info);
        Imagenes.cargarAvatar(holder.ivAvatar, perfil.fotoUrl);
        holder.ivAvatar.setOnClickListener(v ->
                VerFotoActivity.abrirPublica(v.getContext(), perfil.fotoUrl, perfil.nombre));
        holder.itemView.setOnClickListener(v -> alSeleccionar.contacto(perfil));
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final TextView tvNombre;
        final TextView tvInfo;

        ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvInfo = itemView.findViewById(R.id.tvInfo);
        }
    }
}
