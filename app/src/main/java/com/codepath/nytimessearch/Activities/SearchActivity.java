package com.codepath.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.nytimessearch.Adapters.ArticleAdapter;
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

    private RecyclerView rvArticles;
    private ArticleAdapter articleAdapter;

    ArrayList<Article> articles;

    boolean useFilter;
    Filters filter;
    String query = "Android";

    Handler handler = new Handler();
    EndlessRecyclerViewScrollListener endlessScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_r);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    public void setupViews(){

        articles = new ArrayList<>();

        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        articleAdapter = new ArticleAdapter(this, articles);

        rvArticles.setAdapter(articleAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(layoutManager);

        endlessScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        rvArticles.addOnScrollListener(endlessScrollListener);

        //to create articles in the list;
        makeQuery();
    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data) {
        //We got new value for an item
        if (result_code == RESULT_OK && request_code == REQUEST_CODE){
            this.filter = (Filters) data.getSerializableExtra("filter");
            this.useFilter = true;
            Log.d(TAG, "use filter");

            //reset page
            resultsPage = 0;
            clearResults();
            endlessScrollListener.resetState();
            makeQuery();
        }
        else{
            Log.d(TAG,"REQUEST CODE or/and RESULT CODE not good: REQUEST_CODE=" + REQUEST_CODE + "RESULT_OK =" + RESULT_OK);
        }


    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    onArticleSearch(query);

                    // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                    // see https://code.google.com/p/android/issues/detail?id=24599
                    searchView.clearFocus();

                    return true;
                }


                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.isEmpty())
                        query = "";
                    return false;
                }


            });

            return super.onCreateOptionsMenu(menu);

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


    public void onArticleSearch(String query) {
        this.query = query;
        clearResults();
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
                    articles.addAll(Article.fromJSONArray(articlesJSONResults));
                    displayQuery();
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
        resultsPage = 0;
        articles.clear();
        articleAdapter.notifyDataSetChanged();
        endlessScrollListener.resetState();
    }

    private void displayQuery(){
        Log.d(TAG, articles.size() + " articles in arrayList");
        if(articles.size() == 0)
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show();
//        adapter.clear();
//        adapter.addAll(articles);
        articleAdapter.notifyDataSetChanged();

        Log.d(TAG, articleAdapter.toString());
    }


    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
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

        RecyclerView.LayoutManager mLayoutManager;

        public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                }
                else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount, view);
                loading = true;
            }
        }

        // Call this method whenever performing new searches
        public void resetState() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;
            this.loading = true;
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

    }
}
