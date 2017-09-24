package com.codepath.nytimessearch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytimessearch.Activities.ArticleActivity;
import com.codepath.nytimessearch.Models.Article;
import com.codepath.nytimessearch.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by emilie on 9/23/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{

    final String TAG = "ArticleAdapterTAG";
    private ArrayList<Article> mArticles;
    private Context mContext;

    public ArticleAdapter(Context context, ArrayList<Article> articles){
        mArticles = articles;
        mContext = context;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = layoutInflater.inflate(R.layout.article_item_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(context, articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticles.get(position);

        holder.headline.setText(article.getHeadLine());

        String thumbnail = article.getThumbNail();

        Picasso.Builder builder = new Picasso.Builder(getContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }});

        Picasso picasso = builder.build();

        if(!TextUtils.isEmpty(thumbnail)){
            Log.d(TAG, "ThumbNail to display is= " +  thumbnail);
            picasso.load(thumbnail).fit().error(R.mipmap.ic_launcher).into(holder.thumbnail, new Callback() {
                @Override
                public void onSuccess() {

                    Log.d("TAG", "onSuccess");
                }

                @Override
                public void onError() {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            holder.thumbnail.setImageResource(R.drawable.ic_broken_image);
        }


    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView thumbnail;
        public TextView headline;

        private Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            thumbnail = itemView.findViewById(R.id.ivImage);
            headline = itemView.findViewById(R.id.tvTitle);

            this.context = context;

            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Intent intent = new Intent(context, ArticleActivity.class);
                Article article = mArticles.get(position);
                intent.putExtra("article", article);

                context.startActivity(intent);
            }
        }

    }


}
