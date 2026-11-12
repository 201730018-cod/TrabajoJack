package com.jackbrandon.chatea.ui.chat;

import com.jackbrandon.chatea.data.model.Mensaje;
import com.jackbrandon.chatea.ui.Fechas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ElementoChat {

    enum Entrega { ENVIADO, RECIBIDO, LEIDO }

    static final int FECHA = 0;
    static final int ENVIADO = 1;
    static final int RECIBIDO = 2;

    final int tipo;
    final String clave;
    final String etiquetaFecha;
    final Mensaje mensaje;
    final Entrega entrega;
    final String nombreRemitente;

    private ElementoChat(int tipo, String clave, String etiquetaFecha, Mensaje mensaje,
                         Entrega entrega, String nombreRemitente) {
        this.tipo = tipo;
        this.clave = clave;
        this.etiquetaFecha = etiquetaFecha;
        this.mensaje = mensaje;
        this.entrega = entrega;
        this.nombreRemitente = nombreRemitente;
    }

    static List<ElementoChat> armar(List<Mensaje> mensajes, String miId, String otroRecibidoEn,
                                    String otroLeidoEn, Map<String, String> nombresDelGrupo) {
        List<ElementoChat> elementos = new ArrayList<>();
        String diaAnterior = null;
        String remitenteAnterior = null;

        for (Mensaje m : mensajes) {
            Date fecha = Fechas.desdeSupabase(m.creadoEn);
            if (fecha != null) {
                String dia = Fechas.claveDia(fecha);
                if (!dia.equals(diaAnterior)) {
                    elementos.add(new ElementoChat(FECHA, "fecha-" + dia, Fechas.etiquetaDia(fecha), null, null, null));
                    diaAnterior = dia;
                    remitenteAnterior = null;
                }
            }

            if (miId != null && miId.equals(m.remitenteId)) {
                Entrega entrega = Entrega.ENVIADO;
                if (Fechas.noAntesDe(otroLeidoEn, m.creadoEn)) {
                    entrega = Entrega.LEIDO;
                } else if (Fechas.noAntesDe(otroRecibidoEn, m.creadoEn)) {
                    entrega = Entrega.RECIBIDO;
                }
                elementos.add(new ElementoChat(ENVIADO, m.id, null, m, entrega, null));
            } else {
                String nombre = null;
                if (nombresDelGrupo != null && !Objects.equals(m.remitenteId, remitenteAnterior)) {
                    nombre = nombresDelGrupo.get(m.remitenteId);
                    if (nombre == null) nombre = "Ex participante";
                }
                elementos.add(new ElementoChat(RECIBIDO, m.id, null, m, null, nombre));
            }
            remitenteAnterior = m.remitenteId;
        }
        return elementos;
    }

    boolean mismoContenido(ElementoChat otro) {
        return tipo == otro.tipo
                && entrega == otro.entrega
                && Objects.equals(nombreRemitente, otro.nombreRemitente)
                && Objects.equals(etiquetaFecha, otro.etiquetaFecha)
                && Objects.equals(mensaje, otro.mensaje);
    }
}
