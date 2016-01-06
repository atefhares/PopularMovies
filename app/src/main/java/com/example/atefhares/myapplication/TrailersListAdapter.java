package com.example.atefhares.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Atef Hares on 30-Dec-15.
 */
public class TrailersListAdapter extends BaseAdapter {
    String[] Titles;
    String[] Videospathes;
    Context context;

    private static LayoutInflater inflater = null;

    public TrailersListAdapter(Activity mainActivity, String[] titles, String[] videospathes) {
        Titles = new String[titles.length];
        Titles = titles;
        Videospathes = new String[videospathes.length];
        Videospathes = videospathes;
        context = mainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Titles.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView title;
        ImageView play_video;
        ImageView share;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = convertView;
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.tr_list_item_layout, null);
            holder.title = (TextView) rowView.findViewById(R.id.textView);
            holder.title.setText(Titles[position]);
            holder.play_video = (ImageView) rowView.findViewById(R.id.imageView);
            holder.play_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URL = "https://www.youtube.com/watch?v=" + Videospathes[position];
                    Intent URLIntent = new Intent(Intent.ACTION_VIEW);
                    URLIntent.setData(Uri.parse(URL));
                    context.startActivity(URLIntent);
                }
            });

            holder.share = (ImageView) rowView.findViewById(R.id.share);
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URL = "https://www.youtube.com/watch?v=" + Videospathes[position];
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, URL);
                    context.startActivity(shareIntent);
                }
            });
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        return rowView;
    }

}
