package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.newsapp.Adapters.ArticlesListAdapter;
import com.example.android.newsapp.Models.Article;
import com.example.android.newsapp.Networking.BookLoader;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.Networking.NetworkUtils.getStringFromResources;
import static com.example.android.newsapp.Networking.NetworkUtils.isConnectedToNet;
import static com.example.android.newsapp.UtilsLibraries.Constants.API_KEY_PARAMETER;
import static com.example.android.newsapp.UtilsLibraries.Constants.BOOKS_LISTING_URL;
import static com.example.android.newsapp.UtilsLibraries.Constants.NEWS_LOADER_ID;
import static com.example.android.newsapp.UtilsLibraries.Constants.NEWS_SUBJECT;
import static com.example.android.newsapp.UtilsLibraries.Constants.QUERY_PARAMETER;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    private View loadingIndicator;
    private TextView emptyTextView;
    private ListView listView;
    private Button noNetRetryButton;
    private ArticlesListAdapter articlesAdapter;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUiComponents();
        initializeArticlesListAndSetListenerOnListItems();
        checkConnectionToNetAndFetchData();
    }

    private void initializeUiComponents() {
        setContentView(R.layout.activity_main);
        loadingIndicator = findViewById(R.id.loadingSpinnerId);
        emptyTextView = (TextView) findViewById(R.id.emptyViewId);
        listView = (ListView) findViewById(R.id.baseListId);
        listView.setEmptyView(emptyTextView);
        noNetRetryButton = (Button) findViewById(R.id.retryButtonId);
    }

    private void initializeArticlesListAndSetListenerOnListItems() {
        final List<Article> articles = new ArrayList<>();
        articlesAdapter = new ArticlesListAdapter(this, articles);
        listView.setAdapter(articlesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openWebPage(articles.get(position).getWebUrl());
            }
        });
    }

    private void checkConnectionToNetAndFetchData() {
        if (isConnectedToNet(this)) {
            emptyTextView.setVisibility(View.GONE);
            noNetRetryButton.setVisibility(View.GONE);
            initializeLoaderAndFetchData();
        } else {
            articlesAdapter.clear();
            loadingIndicator.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getStringFromResources(this, R.string.no_internet_connection_message));
            noNetRetryButton.setVisibility(View.VISIBLE);
            setListenerOnRetryButton();
        }
    }

    private void setListenerOnRetryButton() {
        noNetRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnectionToNetAndFetchData();
            }
        });
    }

    private void initializeLoaderAndFetchData() {
        loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, webpage);
        if (urlIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(urlIntent);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        emptyTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        return new BookLoader(this, provideFinalUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        loadingIndicator.setVisibility(View.GONE);
        articlesAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            articlesAdapter.addAll(articles);
        } else {
            emptyTextView.setText(getStringFromResources(this, R.string.empty_list_message));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        articlesAdapter.clear();
    }

    private String provideFinalUrl() {
        Uri baseUri = Uri.parse(BOOKS_LISTING_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(QUERY_PARAMETER, NEWS_SUBJECT);
        uriBuilder.appendQueryParameter(API_KEY_PARAMETER, getStringFromResources(this, R.string.The_Guardian_API_key));
        return uriBuilder.toString();
    }
}
