package com.kadek.chtsys;

/**
 * Created by kuro on 2/25/18.
 */

public class Messages {

    private String message, type;
    private long time;
    private boolean seen;

    private String from;

    public String getFrom() {
        return from;
    }

    public Messages (String from) {
        this.from = from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isSeen() {
        return seen;
    }

    public Messages(String message, boolean seen, long time, String type){
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Messages(){

    }
}
