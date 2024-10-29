package com.worthybitbuilders.squadsense.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.ProjectTemplates;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivityViewModel extends ViewModel {
    ProjectService projectService = RetrofitServices.getProjectService();
    private ProjectModel projectModel = null;
    private final MutableLiveData<ProjectModel> projectModelLiveData = new MutableLiveData<>();

    public ProjectActivityViewModel() {}

    public void getProjectById(String projectId, ApiCallHandlers handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
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

    public String getProjectId() {
        return projectModel.get_id();
    }

    /**
     * This method is called when user create a new project
     * The new project is automatically created by this method
     * which is then saved in the view model
     */
    public void saveNewProjectToRemote(ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
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

    public void addNewBoardToProject(ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
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

    public void updateBoardTitle(int boardPosition, String newTitle, ApiCallHandlers handlers) throws JSONException {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        JSONObject data = new JSONObject();
        data.put("boardTitle", newTitle);
        Call<Void> call = projectService.updateBoard(userId, getProjectId(), getProjectModel().getBoards().get(boardPosition).get_id(), data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getProjectModel().getBoards().get(boardPosition).setBoardTitle(newTitle);
                    handlers.onSuccess();
                } else handlers.onFailure(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void removeBoard(int boardPosition, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.removeBoard(userId, getProjectId(), getProjectModel().getBoards().get(boardPosition).get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getProjectModel().removeBoardAt(boardPosition);
                    handlers.onSuccess();
                } else handlers.onFailure(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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

    public interface ApiCallHandlers {
        void onSuccess();
        void onFailure(String message);
    }
}
