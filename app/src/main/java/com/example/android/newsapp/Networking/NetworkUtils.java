package com.example.android.newsapp.Networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.newsapp.Models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.android.newsapp.UtilsLibraries.Constants.CONNECTION_TIMEOUT;
import static com.example.android.newsapp.UtilsLibraries.Constants.LOG_TAG;
import static com.example.android.newsapp.UtilsLibraries.Constants.READ_TIMEOUT;

public class NetworkUtils {

    public static boolean isConnectedToNet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnected());
        return isConnected;
    }

    public static String getStringFromResources(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    public static List<Article> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Article> articles = extractFeatureFromJson(jsonResponse);
        return articles;
    }

    public static List<Article> extractFeatureFromJson(String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }
        List<Article> articles = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(requestUrl);
            if (jsonObj.has("response")) {
                JSONObject response = jsonObj.getJSONObject("response");
                if (response.has("results")) {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject currentArticle = results.getJSONObject(i);
                        String articleTitle = "Title N/A";
                        if (currentArticle.has("webTitle")) {
                            articleTitle = currentArticle.getString("webTitle");
                        }
                        String articleSection = "Section N/A";
                        if (currentArticle.has("sectionName")) {
                            articleSection = currentArticle.getString("sectionName");
                        }
                        String articlePublishedDate = "Published date N/A";
                        if (currentArticle.has("webPublicationDate")) {
                            articlePublishedDate = dateFormatter(currentArticle.getString("webPublicationDate"));
                        }
                        String articleWebUrl = "";
                        if (currentArticle.has("webUrl")) {
                            articleWebUrl = currentArticle.getString("webUrl");
                        }
                        articles.add(new Article(articleTitle, articleSection, articlePublishedDate, articleWebUrl));
                    }
                }
            }
        } catch (JSONException | ParseException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return articles;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static String dateFormatter(String stringDate) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        Date date = format.parse(stringDate);
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMMM d, y, h:mm a");
        return timeFormat.format(date);
    }
}
