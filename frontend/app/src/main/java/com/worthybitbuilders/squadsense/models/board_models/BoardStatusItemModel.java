package com.worthybitbuilders.squadsense.models.board_models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardStatusItemModel extends BoardBaseItemModel {
    private List<String> contents;
    private List<String> colors;
    public BoardStatusItemModel() {
        super("", "CellStatus");
        this.contents = new ArrayList<>();
        this.colors = new ArrayList<>();
    }

    // TODO: JUST FOR SAMPLE DATA BOARD, REMOVE IT IF SAMPLE BOARD IS NOT USED
    public BoardStatusItemModel(String content, List<String> contents) {
        super(content, "CellStatus");
        this.contents = contents;
        this.colors = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) colors.add("#9c9c9c");
    }

    public BoardStatusItemModel(String content, List<String> contents, List<String> colors) {
        super(content, "CellStatus");
        this.contents = contents;
        this.colors = colors;
    }
    public BoardStatusItemModel(BoardStatusItemModel itemModel) {
        super(itemModel.getContent(), "CellStatus");
        this.contents = itemModel.getContents();
        this.colors = itemModel.getColors();
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContentAt(int position, String newContent) {
        this.contents.set(position, newContent);
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColorAt(int index, String newColor) {
        this.colors.set(index, newColor);
    }

    public String getColorAt(int index) {
        try {
            return colors.get(index);
        } catch (Exception e) {
            return "#9c9c9c";
        }
    }

    public void addNewContent(String newContent) {
        contents.add(newContent);
        colors.add("#9c9c9c");
    }

    public void copyDataFromAnotherInstance(BoardStatusItemModel itemModel) {
        setContent(itemModel.getContent());
        setColors(itemModel.getColors());
        setContents(itemModel.getContents());
    }

    public void removeContentAt(int position) {
        String deletedContent = contents.remove(position);
        if (Objects.equals(deletedContent, getContent())) {
            setContent("");
        }
        colors.remove(position);
    }
}
