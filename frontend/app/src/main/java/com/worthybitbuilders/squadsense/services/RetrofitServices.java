package com.worthybitbuilders.squadsense.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

import java.lang.reflect.Type;

import okhttp3.internal.Util;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServices {
    private static UserService userService = null;
    private static FriendService friendService = null;
    private static ChatRoomService chatRoomService = null;
    private static NotificationService notificationService = null;
    private static ProjectService projectService = null;
    private static UtilService utilService = null;

    private static ReportService reportService = null;
    private final static String BASE_URL = "http://192.168.0.5:3000/";
    
    private static final Gson mGson = new GsonBuilder()
            .registerTypeAdapter(BoardBaseItemModel.class, new BoardCellDeserializer())
            .create();

    private static final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(mGson))
                    .build();

    public static ReportService getReportService(){
        if (reportService == null) reportService = retrofit.create(ReportService.class);
        return reportService;
    }

    public static UserService getUserService() {
        if (userService == null) userService = retrofit.create(UserService.class);
        return userService;
    }

    public static FriendService getFriendService() {
        if (friendService == null) friendService = retrofit.create(FriendService.class);
        return friendService;
    }

    public static NotificationService getNotificationService() {
        if (notificationService == null) notificationService = retrofit.create(NotificationService.class);
        return notificationService;
    }

    public static ChatRoomService getChatRoomService() {
        if (chatRoomService == null) chatRoomService = retrofit.create(ChatRoomService.class);
        return chatRoomService;
    }
    
    public static ProjectService getProjectService() {
        if (projectService == null) projectService = retrofit.create(ProjectService.class);
        return projectService;
    }

    public static UtilService getUtilService() {
        if (utilService == null) utilService = retrofit.create(UtilService.class);
        return utilService;
    }

    private static class BoardCellDeserializer implements JsonDeserializer<BoardBaseItemModel> {
        @Override
        public BoardBaseItemModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            switch (jsonObject.get("cellType").getAsString()) {
                case "CellStatus":
                    return context.deserialize(jsonObject, BoardStatusItemModel.class);
                case "CellUpdate":
                    return context.deserialize(jsonObject, BoardUpdateItemModel.class);
                case "CellText":
                    return context.deserialize(jsonObject, BoardTextItemModel.class);
                case "CellNumber":
                    return context.deserialize(jsonObject, BoardNumberItemModel.class);
                case "CellTimeline":
                    return context.deserialize(jsonObject, BoardTimelineItemModel.class);
                case "CellDate":
                    return context.deserialize(jsonObject, BoardDateItemModel.class);
                case "CellUser":
                    return context.deserialize(jsonObject, BoardUserItemModel.class);
                case "CellCheckbox":
                    return context.deserialize(jsonObject, BoardCheckboxItemModel.class);
                case "CellMap":
                    return context.deserialize(jsonObject, BoardMapItemModel.class);
            }

            throw new IllegalArgumentException();
        }
    }
}
