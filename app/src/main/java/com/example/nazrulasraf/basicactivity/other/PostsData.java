package com.example.nazrulasraf.basicactivity.other;

public class PostsData {

    //This is the model class for Posts.
    private String title, content, username, uid;
    private Long timestamp;


    public PostsData(String uid, String title, String content, String username, Long timestamp) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public PostsData() {

    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
