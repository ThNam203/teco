package com.worthybitbuilders.squadsense.utils;

import android.content.Context;
import android.widget.Toast;

import org.webrtc.EglBase;

import java.util.ArrayList;
import java.util.List;

public class EventChecker {
    private List<Boolean> eventStatusList;
    private CompleteCallback completeCallback;
    public EventChecker() {
        eventStatusList = new ArrayList<>();
    }

    public void setActionWhenComplete(CompleteCallback callback)
    {
        this.completeCallback = callback;
    }
    public int addEventStatusAndGetCode() {
        eventStatusList.add(false);
        return eventStatusList.size() - 1;
    }

    public void markEventAsCompleteAndDoActionIfNeeded(int eventIndex) {
        if (eventIndex >= 0 && eventIndex < eventStatusList.size()) {
            eventStatusList.set(eventIndex, true);
        }
        for (boolean isComplete : eventStatusList) {
            if (!isComplete) {
                return;
            }
        }
        this.completeCallback.Action();
    }
    public interface CompleteCallback{
        void Action();
    }
}
