package com.jackbrandon.chatea.ui;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class Fechas {

    private Fechas() {}

    public static Date desdeSupabase(String texto) {
        long micros = microsegundos(texto);
        return micros == Long.MIN_VALUE ? null : new Date(micros / 1000);
    }

    public static long microsegundos(String texto) {
        if (texto == null || texto.length() < 19) return Long.MIN_VALUE;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        formato.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date segundos;
        try {
            segundos = formato.parse(texto.substring(0, 19).replace(' ', 'T'));
        } catch (ParseException e) {
            return Long.MIN_VALUE;
        }
        if (segundos == null) return Long.MIN_VALUE;

        long fraccion = 0;
        int digitos = 0;
        if (texto.length() > 19 && texto.charAt(19) == '.') {
            for (int i = 20; i < texto.length() && Character.isDigit(texto.charAt(i)) && digitos < 6; i++) {
                fraccion = fraccion * 10 + (texto.charAt(i) - '0');
                digitos++;
            }
        }
        while (digitos < 6) {
            fraccion *= 10;
            digitos++;
        }
        return segundos.getTime() * 1000 + fraccion;
    }

    public static boolean noAntesDe(String fecha, String referencia) {
        long a = microsegundos(fecha);
        long b = microsegundos(referencia);
        return a != Long.MIN_VALUE && b != Long.MIN_VALUE && a >= b;
    }

    public static String ultimaConexion(String textoSupabase) {
        Date fecha = desdeSupabase(textoSupabase);
        if (fecha == null) return "";
        if (System.currentTimeMillis() - fecha.getTime() < 70_000) return "en línea";
        if (DateUtils.isToday(fecha.getTime())) return "últ. vez hoy a las " + hora(fecha);
        if (esAyer(fecha)) return "últ. vez ayer a las " + hora(fecha);
        return "últ. vez el " + new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(fecha)
                + " a las " + hora(fecha);
    }

    public static String ahoraParaSupabase() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'", Locale.US);
        formato.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formato.format(new Date());
    }

    public static String paraListaDeChats(String textoSupabase) {
        Date fecha = desdeSupabase(textoSupabase);
        if (fecha == null) return "";

        if (DateUtils.isToday(fecha.getTime())) {
            return hora(fecha);
        }
        if (esAyer(fecha)) {
            return "Ayer";
        }
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(fecha);
    }

    public static String hora(String textoSupabase) {
        Date fecha = desdeSupabase(textoSupabase);
        return fecha == null ? "" : hora(fecha);
    }

    public static String etiquetaDia(Date fecha) {
        if (DateUtils.isToday(fecha.getTime())) return "Hoy";
        if (esAyer(fecha)) return "Ayer";
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(fecha);
    }

    public static String claveDia(Date fecha) {
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(fecha);
    }

    private static String hora(Date fecha) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);
    }

    private static boolean esAyer(Date fecha) {
        Calendar ayer = Calendar.getInstance();
        ayer.add(Calendar.DAY_OF_YEAR, -1);
        Calendar dia = Calendar.getInstance();
        dia.setTime(fecha);
        return ayer.get(Calendar.YEAR) == dia.get(Calendar.YEAR)
                && ayer.get(Calendar.DAY_OF_YEAR) == dia.get(Calendar.DAY_OF_YEAR);
    }
}
