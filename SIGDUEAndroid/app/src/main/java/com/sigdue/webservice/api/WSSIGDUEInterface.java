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
    Call<ResponseBody> loginUsuarios(@Body Usuario usuario);

    Call<ResponseBody> crearpredial(Predial predialJSON);

    Call<ResponseBody> crearmultimedia(int idPredial, String name, MultipartBody.Part multimedia);
}
