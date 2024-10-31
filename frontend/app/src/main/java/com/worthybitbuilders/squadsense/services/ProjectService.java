package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.BoardDetailItemModel;
import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.models.NewColumnRequestModel;
import com.worthybitbuilders.squadsense.models.NewRowRequestModel;
import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProjectService {
    @GET("{userId}/project")
    Call<List<MinimizedProjectModel>> getAllProject(@Path("userId") String userId);

    @GET("{userId}/project/{projectId}")
    Call<ProjectModel> getProjectById(@Path("userId") String userId, @Path("projectId") String projectId);

    @POST("{userId}/project")
    Call<ProjectModel> saveProject(@Path("userId") String userId, @Body ProjectModel projectModel);

    /** Send a request and get a new empty board from server */
    @POST("{userId}/project/{projectId}/board")
    Call<BoardContentModel> createAndGetNewBoardToProject(@Path("userId") String userId, @Path("projectId") String projectId);

    @PUT("{userId}/project/{projectId}/board/{boardId}")
    Call<Void> updateBoard(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Body JSONObject newTitle);

    @DELETE("{userId}/project/{projectId}/board/{boardId}")
    Call<Void> removeBoard(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId);

    /** The List<String> is the cell ids that are returned from server */
    @PUT("{userId}/project/{projectId}/board/{boardId}/column")
    Call<List<String>> addNewColumnToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Body NewColumnRequestModel newColumn);

    @DELETE("{userId}/project/{projectId}/board/{boardId}/column/{columnPosition}")
    Call<Void> deleteAColumn(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("columnPosition") int columnPosition);

    @PUT("{userId}/project/{projectId}/board/{boardId}/column/{columnPosition}")
    Call<Void> updateAColumn(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("columnPosition") int columnPosition, @Body JSONObject newDescription);

    /** The List<String> is the cell ids that are returned from server */
    @PUT("{userId}/project/{projectId}/board/{boardId}/row")
    Call<List<String>> addNewRowToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Body NewRowRequestModel newRow);

    @GET("{userId}/project/{projectId}/board/{boardId}/row/{rowPosition}")
    Call<BoardDetailItemModel> getCellsInARow(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("rowPosition") Integer rowPosition);

    @PUT("{userId}/project/{projectId}/board/{boardId}/row/{rowPosition}")
    Call<Void> updateRowTitle(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("rowPosition") Integer rowPosition, @Body JSONObject rowTitle);

    @DELETE("{userId}/project/{projectId}/board/{boardId}/row/{rowPosition}")
    Call<Void> deleteARow(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("rowPosition") Integer rowPosition);

    @GET("{userId}/project/{projectId}/board/{boardId}/cell-update/{cellId}")
    Call<List<UpdateTask>> getAllUpdateTasksOfACell(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId);

    @PATCH("{userId}/project/{projectId}/board/{boardId}/cell-update/{cellId}/{updateTaskId}")
    Call<Void> toggleLikeUpdateTask(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Path("updateTaskId") String updateTaskId);

    @Multipart
    @POST("{userId}/project/{projectId}/board/{boardId}/cell-update/{cellId}")
    Call<Void> createNewUpdateTaskToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Part List<MultipartBody.Part> files, @Part("taskContent") UpdateTask taskContent);

    @DELETE("{userId}/project/{projectId}/board/{boardId}/cell-update/{cellId}/{updateTaskId}")
    Call<Void> deleteUpdateTask(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Path("updateTaskId") String updateTaskId);

    // CELL UPDATES
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardStatusItemModel cellModel);
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardTextItemModel cellModel);
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardNumberItemModel cellModel);
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardTimelineItemModel cellModel);
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardDateItemModel cellModel);
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardUserItemModel cellModel);
    @PUT("{userId}/project/{projectId}/board/{boardId}/cell/{cellId}")
    Call<Void> updateCellToRemote(@Path("userId") String userId, @Path("projectId") String projectId, @Path("boardId") String boardId, @Path("cellId") String cellId, @Body BoardCheckboxItemModel cellModel);
}
