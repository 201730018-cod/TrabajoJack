package com.jackbrandon.chatea.ui.grupos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.ui.Imagenes;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SeleccionContactosAdapter extends RecyclerView.Adapter<SeleccionContactosAdapter.ViewHolder> {

    public interface AlCambiar {
        void seleccion(int cantidad);
    }

    private final List<Perfil> contactos = new ArrayList<>();
    private final Set<String> seleccionados = new LinkedHashSet<>();
    private final AlCambiar alCambiar;

    public SeleccionContactosAdapter(AlCambiar alCambiar) {
        this.alCambiar = alCambiar;
    }

    public void mostrar(List<Perfil> lista) {
        contactos.clear();
        contactos.addAll(lista);
        notifyDataSetChanged();
    }

    public List<String> idsSeleccionados() {
        return new ArrayList<>(seleccionados);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto_seleccionable, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Perfil perfil = contactos.get(position);
        holder.tvNombre.setText(perfil.nombre);
        holder.tvInfo.setText(perfil.info);
        holder.cbSeleccionado.setChecked(seleccionados.contains(perfil.id));
        Imagenes.cargarAvatar(holder.ivAvatar, perfil.fotoUrl);

        holder.itemView.setOnClickListener(v -> {
            if (!seleccionados.remove(perfil.id)) seleccionados.add(perfil.id);
            holder.cbSeleccionado.setChecked(seleccionados.contains(perfil.id));
            alCambiar.seleccion(seleccionados.size());
        });
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final TextView tvNombre;
        final TextView tvInfo;
        final CheckBox cbSeleccionado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            cbSeleccionado = itemView.findViewById(R.id.cbSeleccionado);
        }
    }
}
