package com.sigdue.webservice.api;

import com.sigdue.webservice.modelo.Predial;
import com.sigdue.webservice.modelo.Usuario;
import com.sigdue.webservice.modelo.WSSIGDUEResult;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface WSSIGDUEInterface {

    @POST("ee")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> loginUsuarios(@Body Usuario usuario);

    @POST("predial")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> crearpredial(@Body Predial predialJSON);

    @POST("multimedia")
    @Multipart
    Call<ResponseBody> crearmultimedia(@Header("DANE_SEDE") String daneSede,@Header("FILE_NAME") String nombre,@Header("TIPO_MULTIMEDIA") String tipoArchivo, @Part MultipartBody.Part archivo, @Header("FILE_MIMETYPE") String fileMimeType);

    @GET("clase_predio")
    Call<WSSIGDUEResult> listarClasePredio();

    @GET("clima")
    Call<WSSIGDUEResult> listarClima();

    @GET("con_quien_tenencia")
    Call<WSSIGDUEResult> listarConQuienTieneTenencia();

    @GET("distancia_mts_sede_ppal")
    Call<WSSIGDUEResult> listarDistanciaSedePrincipal();

    @GET("dist_km_centro_poblado")
    Call<WSSIGDUEResult> listarDistanciaCentroPoblado();

    @GET("propiedad_lote")
    Call<WSSIGDUEResult> listarPropiedadLote();

    @GET("tenencia")
    Call<WSSIGDUEResult> listarTenencia();

    @GET("tipo_documento")
    Call<WSSIGDUEResult> listarTipoDocumento();

    @GET("topografia")
    Call<WSSIGDUEResult> listarTopografia();

    @GET("zona_aislamiento")
    Call<WSSIGDUEResult> listarZonaAislamiento();

    @GET("zona_alto_riesgo")
    Call<WSSIGDUEResult> listarZonaRiesgo();

    @GET("zona_proteccion")
    Call<WSSIGDUEResult> listarZonaProteccion();


}
