package com.example.atefhares.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReviewsListAdapter extends BaseAdapter {
    String [] Authors;
    String [] Contents;
    String [] URLs;
    Context context;

    private static LayoutInflater inflater=null;

    public ReviewsListAdapter(Activity mainActivity, String[] Authors, String[] Contents, String[] URLs) {
        // TODO Auto-generated constructor stub
        this.Authors = new String[Authors.length];
        this.Authors=Authors;

        this.Contents= new String[Contents.length];
        this.Contents=Contents;

        this.URLs= new String[URLs.length];
        this.URLs = URLs;

        context=mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Contents.length;
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

    public class Holder
    {
        TextView Author;
        TextView Content;
        TextView cont_reading;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView=convertView;
        if(convertView == null) {
            rowView = inflater.inflate(R.layout.rev_list_item_layout, null);

            holder.Author = (TextView) rowView.findViewById(R.id.authortextView);
            holder.Author.setText(Authors[position]);

            holder.Content = (TextView) rowView.findViewById(R.id.contenttextView);
            holder.Content.setText(Contents[position]);

            holder.cont_reading = (TextView) rowView.findViewById(R.id.cont_red);
            holder.cont_reading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URL = URLs[position];
                    Intent URLIntent = new Intent(Intent.ACTION_VIEW);
                    URLIntent.setData(Uri.parse(URL));
                    context.startActivity(URLIntent);
                }
            });
            rowView.setTag( holder );
        }

        return rowView;
    }
}
