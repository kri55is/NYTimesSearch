package com.codepath.nytimessearch.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytimessearch.Models.Article;
import com.codepath.nytimessearch.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by emilie on 9/18/17.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    final String TAG = "ArticleArrayAdapter";
    private static class ViewHolder{
        ImageView thumbnail;
        TextView headline;
    }

    public ArticleArrayAdapter(Context context, List<Article> articles){
        super(context, android.R.layout.simple_list_item_1);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //get data item from position
        Article article = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null){

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.article_item_result, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.thumbnail = convertView.findViewById(R.id.ivImage);
            viewHolder.headline = convertView.findViewById(R.id.tvTitle);

            convertView.setTag(viewHolder);

        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //populate the information

        viewHolder.headline.setText(article.getHeadLine());

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
            picasso.load(thumbnail).fit().error(R.mipmap.ic_launcher).into(viewHolder.thumbnail, new Callback() {
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
            viewHolder.thumbnail.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }

}
