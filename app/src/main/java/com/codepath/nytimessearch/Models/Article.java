package com.codepath.nytimessearch.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by emilie on 9/18/17.
 */

public class Article implements Serializable{

    String webUrl;
    String headLine;
    String thumbNail;


    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumbNail() {
        return thumbNail;
    }



    public Article(JSONObject jsonObject){
        try{
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimediaJSONArray = jsonObject.getJSONArray("multimedia");

            if(multimediaJSONArray.length() >0){
                JSONObject multimediaJSONObject = multimediaJSONArray.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJSONObject.get("url");

            }
            else
                this.thumbNail = "";
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    public static ArrayList<Article> fromJSONArray (JSONArray jsonArray){
        ArrayList<Article> results = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                results.add(new Article(jsonArray.getJSONObject(i)));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return results;

    }
}
