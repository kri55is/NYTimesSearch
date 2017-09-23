package com.codepath.nytimessearch.Models;

import android.util.Log;

import com.loopj.android.http.RequestParams;

/**
 * Created by emilie on 9/23/17.
 */

public class QueryBuilder {


    private static final String TAG = "QueryBuilderTAG";
    private static final String NYTAPIVersion = "v2";
    private static final String NYTAPIKey = "eb47f252eb564d7ca79b4c60c6f7319d";

    public static String buildURL(){
        //https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=eb47f252eb564d7ca79b4c60c6f7319d
        String url = "http://api.nytimes.com/svc/search/"+ NYTAPIVersion + "/articlesearch.json";
        Log.d(TAG, "url without param: " + url);
        return url;
    }

    public static RequestParams buildParamRequestNoFilter(String query,int page) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", NYTAPIKey);
        requestParams.put("page", page);
        if(!query.isEmpty()) {
            requestParams.put("q", query);
        }
        Log.d(TAG, "PARAMS: " + "api-key=" + NYTAPIKey+ ", page=" + page + ", q=" + query);

        return requestParams;
    }

    public static RequestParams buildParamRequestWithFilter(String query,int page, Filters filter){
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", NYTAPIKey);
        requestParams.put("page", page);
        if(!query.isEmpty()) {
            requestParams.put("q", query);
        }
        Log.d(TAG, "PARAMS: " + "api-key=" + NYTAPIKey+ ", page=" + page + ", q=" + query);

            String beginDate = filter.getBeginDateYearAsString() + filter.getBeginDateMonthAsString() + filter.getBeginDateDayAsString();
            requestParams.put("begin_date", beginDate);
            Log.d(TAG, "PARAMS: " + "begin_date = " + beginDate);

            if (filter.isSortingOldest()){
                requestParams.put("sort", "oldest");
                Log.d(TAG, "PARAMS: " + "sort = " + "oldest");
            }
            else{
                requestParams.put("sort", "newest");
                Log.d(TAG, "PARAMS: " + "sort = " + "newest");
            }

            int numDeskValues =filter.getNewsDeskValues().size();
            Log.d(TAG, "PARAMS: " + "number of numDeskValues= " + numDeskValues);

            if (numDeskValues > 0) {
                    String paramNewsDesk = "news_desk:(";
                    for (int i = 0; i < numDeskValues; i++) {
                        if (i > 0){
                            paramNewsDesk += "%20";
                        }
                        paramNewsDesk += "\""+ filter.getNewsDeskValues().get(i) + "\"";
                    }
                    paramNewsDesk += ")";
                    requestParams.put("fq",paramNewsDesk);
                }

            return requestParams;
    }
}
