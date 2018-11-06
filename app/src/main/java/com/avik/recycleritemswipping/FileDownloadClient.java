package com.avik.recycleritemswipping;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FileDownloadClient {
    @GET("strings.xml")
    Call<ResponseBody> downloadFile();
}

//http://localhost:[port]/