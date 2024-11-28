package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.report_models.ReportModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReportService {
    @GET("report/{projectId}")
    Call<List<ReportModel>> getProjectReport(@Path("projectId") String projectId);
}
