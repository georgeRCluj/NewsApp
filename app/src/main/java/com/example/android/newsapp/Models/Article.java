package com.example.android.newsapp.Models;

public class Article {
    String title;
    String section;
    String publishedDate;
    String webUrl;

    public Article(String title, String section, String publishedDate, String webUrl) {
        this.title = title;
        this.section = section;
        this.webUrl = webUrl;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}
