package com.qadr.clouddatabasesystem;

public class ThesisObject {
    private String url, title, author;

    public ThesisObject(String url, String title, String author) {
        this.url = url;
        this.title = title;
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
