package com.example.android.newsapp.Networking;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.newsapp.Models.Article;

import java.util.List;

import static com.example.android.newsapp.Networking.NetworkUtils.fetchNewsData;

public class BookLoader extends AsyncTaskLoader<List<Article>> {
    String[] urls;

    public BookLoader(Context context, String... urls) {
        super(context);
        this.urls = urls;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        if (urls.length < 1 || urls[0] == null) {
            return null;
        }
        List<Article> articles = fetchNewsData(urls[0]);
        return articles;
    }
}
