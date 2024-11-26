package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.models.ChatMessageRequest;
import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UtilService {
    @Multipart
    @POST("/upload-files")
    Call<List<ChatMessage.MessageFile>> uploadFiles(@Part List<MultipartBody.Part> files);
}
