package com.example.abhinav.newsreader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Abhinav on 21-04-18.
 */

public class customListview extends ArrayAdapter {


    private final Activity context;
    private final ArrayList<String> title;

    private final ArrayList<Bitmap> images;

    public customListview(Activity context, ArrayList<String> title,ArrayList<Bitmap>images ) {
        super(context, R.layout.list_item, title);


        this.context=context;
        this.title=title;
        this.images=images;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        titleText.setText(title.get(position));

        imageView.setImageBitmap(images.get(position));


        return super.getView(position, convertView, parent);
    }
}
