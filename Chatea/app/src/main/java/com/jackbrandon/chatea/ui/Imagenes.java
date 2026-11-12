package com.jackbrandon.chatea.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.jackbrandon.chatea.BuildConfig;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Imagenes {

    private static final int TAMANO_AVATAR = 512;
    private static final int TAMANO_FOTO_CHAT = 1280;

    private Imagenes() {}

    public static void cargarAvatar(ImageView destino, String url) {
        Glide.with(destino)
                .load(url)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .circleCrop()
                .into(destino);
    }

    public static GlideUrl imagenPrivada(Context context, String ruta) {
        String url = BuildConfig.SUPABASE_URL.trim().replaceAll("/+$", "")
                + "/storage/v1/object/authenticated/chat-imagenes/" + ruta;
        SessionManager sesiones = SessionManager.get(context);
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("apikey", BuildConfig.SUPABASE_KEY)
                .addHeader("Authorization", sesiones.bearer())
                .build());
    }

    public static byte[] prepararAvatar(Context context, Uri uri) throws IOException {
        Bitmap original = leer(context, uri, TAMANO_AVATAR);
        int lado = Math.min(original.getWidth(), original.getHeight());
        float escala = Math.min(1f, (float) TAMANO_AVATAR / lado);
        Matrix matriz = new Matrix();
        matriz.postRotate(rotacion(context, uri));
        matriz.postScale(escala, escala);

        Bitmap cuadrada = Bitmap.createBitmap(
                original,
                (original.getWidth() - lado) / 2,
                (original.getHeight() - lado) / 2,
                lado, lado, matriz, true);
        return comprimir(cuadrada, 85);
    }

    public static byte[] prepararParaChat(Context context, Uri uri) throws IOException {
        Bitmap original = leer(context, uri, TAMANO_FOTO_CHAT);
        int mayor = Math.max(original.getWidth(), original.getHeight());
        float escala = Math.min(1f, (float) TAMANO_FOTO_CHAT / mayor);
        Matrix matriz = new Matrix();
        matriz.postRotate(rotacion(context, uri));
        matriz.postScale(escala, escala);

        Bitmap lista = Bitmap.createBitmap(
                original, 0, 0, original.getWidth(), original.getHeight(), matriz, true);
        return comprimir(lista, 80);
    }

    private static Bitmap leer(Context context, Uri uri, int tamanoObjetivo) throws IOException {
        BitmapFactory.Options medidas = new BitmapFactory.Options();
        medidas.inJustDecodeBounds = true;
        try (InputStream entrada = context.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(entrada, null, medidas);
        }

        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inSampleSize = 1;
        while (Math.min(medidas.outWidth, medidas.outHeight) / (opciones.inSampleSize * 2) >= tamanoObjetivo) {
            opciones.inSampleSize *= 2;
        }

        Bitmap bitmap;
        try (InputStream entrada = context.getContentResolver().openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(entrada, null, opciones);
        }
        if (bitmap == null) throw new IOException("La imagen no se pudo leer");
        return bitmap;
    }

    private static int rotacion(Context context, Uri uri) throws IOException {
        try (InputStream entrada = context.getContentResolver().openInputStream(uri)) {
            return entrada == null ? 0 : new ExifInterface(entrada).getRotationDegrees();
        }
    }

    private static byte[] comprimir(Bitmap bitmap, int calidad) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, salida);
        return salida.toByteArray();
    }
}
