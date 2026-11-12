package com.jackbrandon.chatea.data.notificaciones;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.jackbrandon.chatea.MainActivity;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.ui.chat.ChatActivity;
import com.jackbrandon.chatea.ui.home.HomeActivity;

public final class Notificaciones {

    public static final String CANAL_MENSAJES = "mensajes";
    public static final String CANAL_GENERAL = "general";

    private Notificaciones() {}

    public static void crearCanales(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel mensajes = new NotificationChannel(
                CANAL_MENSAJES, "Mensajes", NotificationManager.IMPORTANCE_HIGH);
        mensajes.setDescription("Mensajes nuevos de tus chats");
        mensajes.enableVibration(true);

        NotificationChannel general = new NotificationChannel(
                CANAL_GENERAL, "General", NotificationManager.IMPORTANCE_DEFAULT);
        general.setDescription("Avisos de la aplicación");

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(mensajes);
        manager.createNotificationChannel(general);
    }

    public static boolean tienePermiso(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void mostrar(Context context, String canal, String titulo, String texto) {
        Intent abrirApp = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent alTocar = PendingIntent.getActivity(
                context, 0, abrirApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        publicar(context, (int) System.currentTimeMillis(), canal, titulo, texto, alTocar);
    }

    public static void mostrarMensaje(Context context, String conversacionId, String tipoChat, String otroId,
                                      String nombreChat, String fotoChat, String titulo, String texto) {
        Intent chat = new Intent(context, ChatActivity.class)
                .putExtra(ChatActivity.EXTRA_CONVERSACION, conversacionId)
                .putExtra(ChatActivity.EXTRA_ID, otroId)
                .putExtra(ChatActivity.EXTRA_NOMBRE, nombreChat)
                .putExtra(ChatActivity.EXTRA_FOTO, fotoChat)
                .putExtra(ChatActivity.EXTRA_TIPO, tipoChat);

        PendingIntent alTocar = TaskStackBuilder.create(context)
                .addNextIntent(new Intent(context, HomeActivity.class))
                .addNextIntent(chat)
                .getPendingIntent(idDeConversacion(conversacionId),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        publicar(context, idDeConversacion(conversacionId), CANAL_MENSAJES, titulo, texto, alTocar);
    }

    public static void quitarDeConversacion(Context context, String conversacionId) {
        NotificationManagerCompat.from(context).cancel(idDeConversacion(conversacionId));
    }

    private static int idDeConversacion(String conversacionId) {
        return conversacionId.hashCode();
    }

    private static void publicar(Context context, int id, String canal, String titulo, String texto,
                                 PendingIntent alTocar) {
        if (!tienePermiso(context)) return;

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(context, canal)
                .setSmallIcon(R.drawable.ic_notificacion)
                .setColor(ContextCompat.getColor(context, R.color.verde_oscuro))
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setPriority(CANAL_MENSAJES.equals(canal)
                        ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(alTocar);

        try {
            NotificationManagerCompat.from(context).notify(id, notificacion.build());
        } catch (SecurityException sinPermiso) {
        }
    }
}
