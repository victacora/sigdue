package com.sigdue.webservice.api;

import com.sigdue.R;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WSSIGDUEClient
{
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static Retrofit retrofit;
    private static WSSIGDUEInterface wssigdueInterface;
    private static String urlRestService;
    private static int tiempoTimeOut;

    public static WSSIGDUEInterface getClient() throws Exception {
        if (wssigdueInterface == null) {
            urlRestService = (String) UtilidadesGenerales.leerSharedPreferences(R.string.pref_url_api_key, R.string.pref_url_api_default, UtilidadesGenerales.STRING_TYPE);
            tiempoTimeOut = Integer.parseInt((String) UtilidadesGenerales.leerSharedPreferences(R.string.pref_timeout_key, R.string.pref_timeout_default, UtilidadesGenerales.STRING_TYPE));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(urlRestService)
                    .addConverterFactory(GsonConverterFactory.create());
            httpClient.readTimeout(tiempoTimeOut, TimeUnit.SECONDS);
            httpClient.connectTimeout(tiempoTimeOut, TimeUnit.SECONDS);

            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });
            HttpLoggingInterceptor interceptorLogging = new HttpLoggingInterceptor();
            interceptorLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient.interceptors().add(interceptorLogging);
            retrofit = builder.client(httpClient.build()).build();
            wssigdueInterface = (WSSIGDUEInterface) builder.client(httpClient.build()).build().create(WSSIGDUEInterface.class);
        }
        return wssigdueInterface;
    }


}
