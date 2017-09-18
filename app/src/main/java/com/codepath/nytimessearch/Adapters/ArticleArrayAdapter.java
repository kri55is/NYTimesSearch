package com.codepath.nytimessearch.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.Models.Article;
import com.codepath.nytimessearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by emilie on 9/18/17.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

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
        if(!TextUtils.isEmpty(thumbnail)){
            Picasso.with(getContext()).load(thumbnail).resize(10, 10).into(viewHolder.thumbnail);
        }
        else{
            viewHolder.thumbnail.setImageResource(R.mipmap.ic_launcher);
        }

//        if (convertView == null) {
//
//            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
//            convertView = layoutInflater.inflate(R.layout.article_item_result, parent, false);
//        }
//
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
//        String thumbnail = article.getThumbNail();
//        if(!TextUtils.isEmpty(thumbnail)){
//            Picasso.with(getContext()).load(thumbnail).fit().into(imageView);
//        }
//        else{
//            imageView.setImageResource(R.mipmap.ic_launcher);
//        }

        return convertView;
    }

}
