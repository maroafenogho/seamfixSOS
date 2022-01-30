package com.maro.seamfixsos.tools;

import com.google.gson.JsonObject;
import com.maro.seamfixsos.util.Sos;
import com.maro.seamfixsos.util.UserLocation;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("/api/v1/create")
    Call<JsonObject> sendSos(@Body Sos sos);
}
