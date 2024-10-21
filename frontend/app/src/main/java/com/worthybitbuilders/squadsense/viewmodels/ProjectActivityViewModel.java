package com.worthybitbuilders.squadsense.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.ProjectTemplates;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivityViewModel extends ViewModel {
    ProjectService projectService = RetrofitServices.getProjectService();
    private ProjectModel projectModel = null;
    private MutableLiveData<ProjectModel> projectModelLiveData = new MutableLiveData<>();

    public ProjectActivityViewModel() {}

    public void getProjectById(String projectId, OnGettingProjectFromRemote handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        Call<ProjectModel> project = projectService.getProjectById(userId, projectId);
        project.enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                if (response.isSuccessful()) {
                    projectModel = response.body();
                    projectModelLiveData.setValue(projectModel);
                    handler.onSuccess();
                } else {
                    handler.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ProjectModel> call, Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    /**
     * This method is called when user create a new project
     * The new project is automatically created by this method
     * which is then saved in the view model
     */
    public void saveNewProjectToRemote(OnGettingProjectFromRemote handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        List<String> authors = new ArrayList<>();
        authors.add(userId);

        ProjectModel unsetIdProjectModel = new ProjectModel(0, ProjectTemplates.sampleProjectContent());
        Call<ProjectModel> saveProject = projectService.saveProject(userId, unsetIdProjectModel);
        saveProject.enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                if (response.isSuccessful()) {
                    projectModel = response.body();
                    projectModelLiveData.setValue(projectModel);
                    handlers.onSuccess();
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ProjectModel> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void addNewBoardToProject(OnCreateAndSaveNewBoardToRemote handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        Call<BoardContentModel> call = projectService.createAndGetNewBoardToProject(userId, projectModel.get_id());
        call.enqueue(new Callback<BoardContentModel>() {
            @Override
            public void onResponse(Call<BoardContentModel> call, Response<BoardContentModel> response) {
                if (response.isSuccessful()) {
                    projectModel.addBoard(response.body());
                    projectModelLiveData.setValue(projectModel);
                    handlers.onSuccess();
                } else handlers.onFailure(response.message());
            }

            @Override
            public void onFailure(Call<BoardContentModel> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public ProjectModel getProjectModel() {
        return projectModelLiveData.getValue();
    }

    public MutableLiveData<ProjectModel> getProjectModelLiveData() {
        return projectModelLiveData;
    }

    public interface OnGettingProjectFromRemote {
        void onSuccess();
        void onFailure(String message);
    }

    public interface OnCreateAndSaveNewBoardToRemote {
        void onSuccess();
        void onFailure(String message);
    }
}
