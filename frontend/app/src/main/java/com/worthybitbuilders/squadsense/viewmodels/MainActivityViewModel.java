package com.worthybitbuilders.squadsense.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
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
    private MutableLiveData<List<MinimizedProjectModel>> minimizedProjectsMutableLiveData = new MutableLiveData<>(null);

    public MainActivityViewModel() {}

    public void getAllProjects(GetProjectsFromRemoteHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        Call<List<MinimizedProjectModel>> projectsCall = projectService.getAllProject(userId);
        projectsCall.enqueue(new Callback<List<MinimizedProjectModel>>() {
            @Override
            public void onResponse(Call<List<MinimizedProjectModel>> call, Response<List<MinimizedProjectModel>> response) {
                if (response.isSuccessful()) {
                    minimizedProject = response.body();
                    minimizedProjectsMutableLiveData.setValue(minimizedProject);
                }
            }

            @Override
            public void onFailure(Call<List<MinimizedProjectModel>> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public MutableLiveData<List<MinimizedProjectModel>> getProjectsLiveData() {
        return minimizedProjectsMutableLiveData;
    }

    public interface GetProjectsFromRemoteHandlers {
        void onFailure(String message);
    }
}
