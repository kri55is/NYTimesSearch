package com.codepath.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.nytimessearch.Adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.Models.Article;
import com.codepath.nytimessearch.Models.Filters;
import com.codepath.nytimessearch.Models.QueryBuilder;
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

    //This is the request code of the filters
    private final int REQUEST_CODE = 20;

    private final String TAG = "SearchActivity";

    private int resultsPage= 0;

    public EditText etQuery;
    public GridView gvResults;
    public Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    boolean useFilter;
    Filters filter;
    String query = "";

    Handler handler = new Handler();
    EndlessScrollListener endlessScrollListener;

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

        endlessScrollListener = new EndlessScrollListener(10,resultsPage) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(page);
                return true;
            }
        };
        gvResults.setOnScrollListener(endlessScrollListener);
    }

    // Create the Handler object (on the main thread by default)
    // Define the code block to be executed
    private Runnable makeQueryWithDelay = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            makeQuery();
            Log.d("Handlers", "Called on main thread");
        }
    };
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        resultsPage=offset;
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.

        handler.postDelayed(makeQueryWithDelay, 3000);
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data) {
        //We got new value for an item
        if (result_code == RESULT_OK && request_code == REQUEST_CODE){
            this.filter = (Filters) data.getSerializableExtra("filter");
            this.useFilter = true;
            Log.d(TAG, "use filter");

            //launch the query using the filters
//            if(!query.isEmpty()) {
                //reset page
                resultsPage = 0;
                clearResults();
                endlessScrollListener.resetState();
                makeQuery();
//            }
        }
        else{
            Log.d(TAG,"REQUEST CODE or/and RESULT CODE not good: REQUEST_CODE=" + REQUEST_CODE + "RESULT_OK =" + RESULT_OK);
        }


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
        if (id == R.id.action_filter) {
            Log.d(TAG, "action filter clicked");

            Intent intent= new Intent(this, FiltersActivity.class);
            startActivityForResult(intent, REQUEST_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public String buildURL(){
//        //https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=eb47f252eb564d7ca79b4c60c6f7319d
//        String url = "http://api.nytimes.com/svc/search/"+ NYTAPIVersion + "/articlesearch.json";
//        Log.d(TAG, "url without param: " + url);
//        return url;
//    }
//
//    private RequestParams buildParamRequest(){
//        RequestParams requestParams = new RequestParams();
//        requestParams.put("api-key", NYTAPIKey);
//        requestParams.put("page", resultsPage);
//        if(!query.isEmpty()) {
//            requestParams.put("q", query);
//        }
//        Log.d(TAG, "PARAMS: " + "api-key=" + NYTAPIKey+ ", page=" + resultsPage + ", q=" + query);
//
//        if (this.useFilter){
//            String beginDate = filter.getBeginDateYearAsString() + filter.getBeginDateMonthAsString() + filter.getBeginDateDayAsString();
//            requestParams.put("begin_date", beginDate);
//            if (filter.isSortingOldest()){
//                requestParams.put("sort", "oldest");
//            }
//            else{
//                requestParams.put("sort", "newest");
//            }
//
//            int numDeskValues =filter.getNewsDeskValues().size();
//            if (numDeskValues > 0) {
//                String paramNewsDesk = "news_desk:(";
//                for (int i = 0; i < numDeskValues; i++) {
//                    if (i > 0){
//                        paramNewsDesk += "%20";
//                    }
//                    paramNewsDesk += "\""+ filter.getNewsDeskValues().get(i) + "\"";
//                }
//                paramNewsDesk += ")";
//                requestParams.put("fq",paramNewsDesk);
//            }
//
//        }
//
//        return requestParams;
//    }

    public void onArticleSearch(View view) {
        query = etQuery.getText().toString();
        clearResults();
        endlessScrollListener.resetState();
        makeQuery();

    }

    private void makeQuery(){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = QueryBuilder.buildURL();

        RequestParams requestParams;
        if(this.useFilter) {
            requestParams = QueryBuilder.buildParamRequestWithFilter(query,resultsPage, filter);
        }
        else {
            requestParams = QueryBuilder.buildParamRequestNoFilter(query,resultsPage);
        }

        Log.d(TAG, url + "?" + requestParams.toString());
        client.get(url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articlesJSONResults = null;

                try {
                    articlesJSONResults = response.getJSONObject("response").getJSONArray("docs");
//                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articlesJSONResults));
                    displayQuery();
//                    adapter.clear();
//                    adapter.addAll(articles);
//                    Log.d(TAG, adapter.toString());
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 429){
                    //try again after 5 secondes?
                    handler.postDelayed(makeQueryWithDelay, 5000);
                }
                Toast.makeText(getBaseContext(), "something went wrong" + statusCode, Toast.LENGTH_SHORT).show();
                Log.d(TAG, errorResponse.toString());
            }
        });
    }

    private void clearResults(){
        articles.clear();
        adapter.clear();
    }

    private void displayQuery(){
        Log.d(TAG, articles.size() + " articles in arrayList");
        if(articles.size() == 0)
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show();
        adapter.clear();
        adapter.addAll(articles);
        Log.d(TAG, adapter.toString());
    }


    public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
        // The minimum number of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        public EndlessScrollListener(int visibleThreshold, int startPage) {
            this.visibleThreshold = visibleThreshold;
            this.startingPageIndex = startPage;
            this.currentPage = startPage;
        }

        private void resetState(){
            // The current offset index of data you have loaded
           currentPage = 0;
            // The total number of items in the dataset after the last load
           previousTotalItemCount = 0;
            // True if we are still waiting for the last set of data to load.
           loading = true;
            // Sets the starting page index
           startingPageIndex = 0;

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) { this.loading = true; }
            }
            // If it's still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }

            // If it isn't currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
                loading = onLoadMore(currentPage + 1, totalItemCount);
            }
        }

        // Defines the process for actually loading more data based on page
        // Returns true if more data is being loaded; returns false if there is no more data to load.
        public abstract boolean onLoadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }


    }
}
