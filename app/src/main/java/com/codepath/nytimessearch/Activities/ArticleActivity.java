package com.codepath.nytimessearch.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.nytimessearch.Models.Article;
import com.codepath.nytimessearch.R;

public class ArticleActivity extends AppCompatActivity {

    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        article = (Article) getIntent().getSerializableExtra("article");

        WebView webView = (WebView) findViewById(R.id.wbArticle);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(article.getWebUrl());
                return true;
            }
        });
        webView.loadUrl(article.getWebUrl());
    }

}
