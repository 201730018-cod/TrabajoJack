package com.jackbrandon.chatea.data.remote;

import com.jackbrandon.chatea.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class SupabaseClient {

    private static SupabaseApi api;

    private SupabaseClient() {}

    public static boolean estaConfigurado() {
        return !BuildConfig.SUPABASE_URL.isEmpty() && !BuildConfig.SUPABASE_KEY.isEmpty();
    }

    public static synchronized SupabaseApi getApi() {
        if (api == null) {
            api = crearRetrofit().create(SupabaseApi.class);
        }
        return api;
    }

    private static Retrofit crearRetrofit() {
        HttpLoggingInterceptor logs = new HttpLoggingInterceptor();
        logs.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);
        logs.redactHeader("apikey");
        logs.redactHeader("Authorization");

        OkHttpClient http = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder peticion = original.newBuilder()
                            .header("apikey", BuildConfig.SUPABASE_KEY);
                    if (original.header("Authorization") == null && BuildConfig.SUPABASE_KEY.startsWith("eyJ")) {
                        peticion.header("Authorization", "Bearer " + BuildConfig.SUPABASE_KEY);
                    }
                    return chain.proceed(peticion.build());
                })
                .addInterceptor(logs)
                .build();

        String url = BuildConfig.SUPABASE_URL.endsWith("/")
                ? BuildConfig.SUPABASE_URL
                : BuildConfig.SUPABASE_URL + "/";

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(http)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
