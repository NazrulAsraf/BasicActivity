package com.example.nazrulasraf.basicactivity.other;

public class PostsData {

    private String title, content, username;

    public PostsData(String title, String content, String username){
        this.title = title;
        this.content = content;
        this.username = username;
    }

    public PostsData(){

    }

    public void setTitle (String title){
        this.title = title;
    }
    public void setContent (String content){
        this.content = content;
    }
    public void setUsername (String username){
        this.username = username;
    }
    public String getTitle (){
        return title;
    }
    public String getContent (){
        return content;
    }
    public String getUsername (){
        return username;
    }
}
