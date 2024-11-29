package com.worthybitbuilders.squadsense.viewmodels;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.models.ErrorResponse;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.ProjectTemplates;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivityViewModel extends ViewModel {
    ProjectService projectService = RetrofitServices.getProjectService();
    private ProjectModel projectModel = null;
    // when starting the activity
    // we set the initial value for chosenPosition (boardPosition)
    // after that we dont
    private transient boolean hasSetChosenPositionInitially = false;
    private int initialChosenBoardPosition;

    public void setInitialChosenBoardPosition(int initialChosenBoardPosition) {
        this.initialChosenBoardPosition = initialChosenBoardPosition;
    }

    private final MutableLiveData<ProjectModel> projectModelLiveData = new MutableLiveData<>();
    private String projectId;
    public ProjectActivityViewModel(String projectId) {
        this.projectId = projectId;
    }

    public ProjectActivityViewModel() {}

    public void getProjectById(String projectId, ApiCallHandlers handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<ProjectModel> project = projectService.getProjectById(userId, projectId);
        project.enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                if (response.isSuccessful()) {
                    int previousChosenBoardPosition = -1;
                    if (projectModel != null) previousChosenBoardPosition = projectModel.getChosenPosition();

                    projectModel = response.body();
                    if (projectModel == null) {
                        projectModelLiveData.setValue(null);
                        return;
                    }

                    if (!hasSetChosenPositionInitially) {
                        projectModel.setChosenPosition(initialChosenBoardPosition);
                        hasSetChosenPositionInitially = true;
                    }
                    else projectModel.setChosenPosition(previousChosenBoardPosition);
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
        return projectId;
    }

    /**
     * This method is called when user create a new project
     * The new project is automatically created by this method
     * which is then saved in the view model
     */
    public void saveNewProjectToRemote(ApiCallHandlers handlers, String templateName) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        List<String> authors = new ArrayList<>();
        authors.add(userId);
        ProjectModel unsetIdProjectModel;
        if (templateName.equals("defaultTemplate"))
            unsetIdProjectModel = new ProjectModel(0, ProjectTemplates.sampleProjectContent());
        else if (templateName.equals("ITManagement"))
            unsetIdProjectModel = new ProjectModel(0, ProjectTemplates.sampleITManagementContent(),"IT Management");
        else if (templateName.equals("FacilitiesRequests")) {
            unsetIdProjectModel = new ProjectModel(0, ProjectTemplates.sampleFacilitiesRequestsContent(),"Facilities Requests");
        }
        else if (templateName.equals("ProjectRequestAndApprovals")) {
            unsetIdProjectModel = new ProjectModel(0, ProjectTemplates.sampleProjectRequestAndApprovalsContent(),"Project Request And Approvals");
        } else if (templateName.equals("RecruitmentAndOnboarding")){
            unsetIdProjectModel = new ProjectModel(0, ProjectTemplates.sampleRecuitmenAndOnboardingContent(),"Recruitment And Onboarding");
        }
        else {
            throw new RuntimeException("Illegal template name");
        }

        Call<ProjectModel> saveProject = projectService.saveProject(userId, unsetIdProjectModel);
        saveProject.enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                if (response.isSuccessful()) {
                    projectModel = response.body();
                    projectModelLiveData.setValue(projectModel);
                    projectId = projectModel.get_id();
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

    public void updateBoardDeadlineColumnIndex(int boardPosition, int deadlineColumnIndex, ApiCallHandlers handlers) throws JSONException {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        JSONObject data = new JSONObject();
        data.put("deadlineColumnIndex", deadlineColumnIndex);
        Call<Void> call = projectService.updateBoard(userId, getProjectId(), getProjectModel().getBoards().get(boardPosition).get_id(), data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getProjectModel().getBoards().get(boardPosition).setDeadlineColumnIndex(deadlineColumnIndex);
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

    public void deleteProject(String projectId, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.deleteProject(userId, projectId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void getMember(String projectId, ApiCallMemberHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<UserModel>> call = projectService.getMember(userId, projectId);
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    List<UserModel> listMember = response.body();
                    handlers.onSuccess(listMember);
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void requestMemberToJoinProject(String projectId, String receiverId, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.requestMemberToJoinProject(userId, projectId, receiverId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void replyToJoinProject(String projectId, String receiverId, String response, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<String> call = projectService.replyToJoinProject(userId, projectId, receiverId, response);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void updateProject(ProjectModel project, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.updateProject(userId, project.get_id() ,project);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void deleteMember(String projectId, String memberId, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.deleteMember(userId, projectId, memberId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void requestAdmin(String projectId, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.requestAdmin(userId, projectId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void replyToAdminRequest(String projectId, String memberId, String response, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.replyToAdminRequest(userId, projectId, memberId, response);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void makeAdmin(String projectId, String memberId, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.makeAdmin(userId, projectId, memberId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void changeAdminToMember(String projectId, String adminId, ApiCallHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.changeAdminToMember(userId, projectId, adminId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handlers.onSuccess();
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
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

    public interface ApiCallMemberHandlers {
        void onSuccess(List<UserModel> listMemberData);
        void onFailure(String message);
    }
}
