package com.example.android.newsapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.newsapp.Models.Article;
import com.example.android.newsapp.R;

import java.util.List;

public class ArticlesListAdapter extends ArrayAdapter<Article> {
    private TextView titleView;
    private TextView sectionView;
    private TextView publishedDateView;
    private Article currentArticle;
    private View listItemView;

    public ArticlesListAdapter(Context context, List<Article> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflateList(position, convertView, parent);
        initializeUiComponents();
        setTextsOnVariables();
        return listItemView;
    }

    private void initializeUiComponents() {
        titleView = (TextView) listItemView.findViewById(R.id.articleTitleId);
        sectionView = (TextView) listItemView.findViewById(R.id.articleSectionId);
        publishedDateView = (TextView) listItemView.findViewById(R.id.articlePublishedDateId);
    }

    private void inflateList(int position, View convertView, ViewGroup parent) {
        currentArticle = getItem(position);
        // Check if the existing view is being reused, otherwise inflate the view
        listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
    }

    private void setTextsOnVariables() {
        titleView.setText(currentArticle.getTitle());
        sectionView.setText(currentArticle.getSection());
        publishedDateView.setText(currentArticle.getPublishedDate());
    }
}
