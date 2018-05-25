package com.example.abhinav.newsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Category_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        final ArrayList<String> types=new ArrayList<>();
        types.add("Business");
        types.add("Entertainment");
        types.add("General");
        types.add("Sports");
        types.add("Technology");
        types.add("Health");
        types.add("Science");

        final ListView typeList=(ListView)findViewById(R.id.category_list);


        ArrayAdapter adapter=new ArrayAdapter(Category_list.this,android.R.layout.simple_list_item_1,types);

        typeList.setAdapter(adapter);


        typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent in=new Intent(getApplicationContext(),MainActivity.class);

                in.putExtra("category",types.get(i));

                startActivity(in);

            }
        });










    }
}
