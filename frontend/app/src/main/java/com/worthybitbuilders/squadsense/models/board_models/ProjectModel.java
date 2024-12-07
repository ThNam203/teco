package com.worthybitbuilders.squadsense.models.board_models;

import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class ProjectModel {
    private String _id;
    private String creatorId;
    private List<String> memberIds;
    private List<String> adminIds;
    private transient int chosenPosition = 0;
    private String title;
    private List<BoardContentModel> boards;
    private String updatedAt;

    /**
     * Mostly used to create an sample project to upload to server
     * the _id will then returned and set
     */
    public ProjectModel(int chosenPosition, List<BoardContentModel> boards) {
        this.title = "Start from scratch";
        this.creatorId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);

        memberIds = new ArrayList<>();
        adminIds = new ArrayList<>();
        memberIds.add(creatorId);
        adminIds.add(creatorId);

        this.chosenPosition = chosenPosition;
        this.boards = boards;
    }

    public ProjectModel(int chosenPosition, List<BoardContentModel> boards,String title) {
        this.title = title;
        this.creatorId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);

        memberIds = new ArrayList<>();
        adminIds = new ArrayList<>();
        memberIds.add(creatorId);
        adminIds.add(creatorId);

        this.chosenPosition = chosenPosition;
        this.boards = boards;
    }

    /**
     * This constructor is used to create when retrieving data from server
     */
    public ProjectModel(String _id, String creatorId, List<String> memberIds, String title, List<BoardContentModel> boards, String updatedAt) {
        this._id = _id;
        this.creatorId = creatorId;
        this.memberIds = memberIds;
        this.title = title;
        this.boards = boards;
        this.updatedAt = updatedAt;
    }

    public int getChosenPosition() {
        return chosenPosition;
    }

    public void setChosenPosition(int chosenPosition) {
        this.chosenPosition = chosenPosition;
    }

    public List<BoardContentModel> getBoards() {
        return boards;
    }

    public void setBoards(List<BoardContentModel> boards) {
        this.boards = boards;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> adminIds) {
        this.memberIds = adminIds;
    }

    public void addBoard(BoardContentModel newBoard) {
        this.boards.add(newBoard);
    }
    public void removeBoardAt(int position) {
        this.boards.remove(position);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(List<String> adminIds) {
        this.adminIds = adminIds;
    }
}
