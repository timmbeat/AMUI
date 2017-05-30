package com.example.tim.sushicounter;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * This Class will show all data from all All you can eat running sushi
 * Created by Tim Mend on 27.05.2017
 */
public class ShowStatistics extends AppCompatActivity {
    int b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_statistics);

        //Get Intent
        Intent intent = getIntent();
        ArrayList<String> datalist = intent.getStringArrayListExtra(Counter.EXTRA_MESSAGE);
        ListView listView = (ListView) findViewById(R.id.statisticlist);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.sushi_items, datalist);
        listView.setAdapter(adapter);
        b = intent.getIntExtra(Counter.AMOUNT_INSTANCE, 0);
        Counter._amount = b;
    }




}
