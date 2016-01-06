package com.example.atefhares.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Atef Hares on 25-Dec-15.
 */
public class ImageAdapter extends BaseAdapter{
    private Context context;
    private String[] imageUrls;

    public void setData(Context c, String[] imgUrls)
    {
        context = c;
        imageUrls = imgUrls;

//        //testing
//        for(String s : imageUrls)
//        {
//            Log.d("ImageAdapter", "moviesImages Pathes: " + s);
//        }
    }

    //---returns the number of images---
    public int getCount() {

        return imageUrls.length;
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        //Log.d("ImageAdapter", "imageUrls: " + imageUrls[position]);
        Picasso.with(this.context)
                .load(imageUrls[position])
                //.placeholder(context.getResources().getDrawable(R.drawable.noimage))
                .error(context.getResources().getDrawable(R.drawable.noimg))
                .into(imageView);
        //Picasso.with(this.context).load(imageUrls[position]).into(imageView);
      /*correct one*/ //Picasso.with(this.context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        // Picasso.with(this.context).load(R.drawable.a).into(imageView);
        //Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        return imageView;
    }
}

