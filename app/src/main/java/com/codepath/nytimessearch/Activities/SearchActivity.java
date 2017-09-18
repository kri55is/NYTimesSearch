package com.codepath.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytimessearch.Adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.Models.Article;
import com.codepath.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private final String NYTAPIVersion = "v2";
    private final String NYTAPIKey = "eb47f252eb564d7ca79b4c60c6f7319d";
    private final String TAG = "SearchActivity";

    private int resultsPage= 0;

    public EditText etQuery;
    public GridView gvResults;
    public Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    public void setupViews(){

        this.etQuery = (EditText) findViewById(R.id.etQuery);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                intent.putExtra("article", article);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getURL(){
        //https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=eb47f252eb564d7ca79b4c60c6f7319d
        String url = "http://api.nytimes.com/svc/search/"+ NYTAPIVersion + "/articlesearch.json";
        Log.d(TAG, "url without param: " + url);
        return url;
    }
    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = getURL();

        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", NYTAPIKey);
        requestParams.put("page", resultsPage);
        requestParams.put("q", query);
        Log.d(TAG, "PARAMS: " + "api-key=" + NYTAPIKey+ ", page=" + resultsPage + ", q=" + query);

        client.get(url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articlesJSONResults = null;

                try {
                    articlesJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    articles = Article.fromJSONArray(articlesJSONResults);
                    adapter.addAll(articles);
                    Log.d(TAG, adapter.toString());
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.d(TAG, errorResponse.toString());
            }
        });
    }
}
