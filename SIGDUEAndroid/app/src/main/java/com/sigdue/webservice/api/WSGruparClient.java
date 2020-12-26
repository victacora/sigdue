package com.sigdue.webservice.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sigdue.Constants.BASE_URL_API;


public class WSGruparClient
{
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static Retrofit retrofit;
    private static WSGruparInterface wsGruparApiInterface;

    public static WSGruparInterface getClient() {
        if (wsGruparApiInterface == null) {
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL_API).addConverterFactory(GsonConverterFactory.create());
            httpClient.readTimeout(5, TimeUnit.MINUTES);
            httpClient.connectTimeout(5, TimeUnit.MINUTES);
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });
            retrofit = builder.client(httpClient.build()).build();
            wsGruparApiInterface = (WSGruparInterface) builder.client(httpClient.build()).build().create(WSGruparInterface.class);
        }
        return wsGruparApiInterface;
    }


}
