package com.jackbrandon.chatea;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.jackbrandon.chatea.data.notificaciones.Notificaciones;
import com.jackbrandon.chatea.data.presencia.Presencia;

public class ChateaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Notificaciones.crearCanales(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new Presencia(this));
    }
}
