package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.models.WorkModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {
    ProjectService projectService = RetrofitServices.getProjectService();
    private List<MinimizedProjectModel> minimizedProject;
    private List<WorkModel> userWorks;

    public MainActivityViewModel() {}

    public List<WorkModel> getUserWorks() {
        return userWorks;
    }

    public void getAllProjects(GetProjectsFromRemoteHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<MinimizedProjectModel>> projectsCall = projectService.getAllProject(userId);
        projectsCall.enqueue(new Callback<List<MinimizedProjectModel>>() {
            @Override
            public void onResponse(Call<List<MinimizedProjectModel>> call, Response<List<MinimizedProjectModel>> response) {
                if (response.isSuccessful()) {
                    minimizedProject = response.body();
                    handlers.onSuccess(minimizedProject);
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<MinimizedProjectModel>> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void getAllUserWork(ApiCallHandler handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<WorkModel>> projectsCall = projectService.getAllWork(userId);
        projectsCall.enqueue(new Callback<List<WorkModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<WorkModel>> call, @NonNull Response<List<WorkModel>> response) {
                if (response.isSuccessful()) {
                    userWorks = response.body();
                    handlers.onSuccess();
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WorkModel>> call, @NonNull Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }


    public interface GetProjectsFromRemoteHandlers {
        void onSuccess(List<MinimizedProjectModel> dataMinimizeProjects);
        void onFailure(String message);
    }

    public interface ApiCallHandler {
        void onSuccess();
        void onFailure(String message);
    }
}
