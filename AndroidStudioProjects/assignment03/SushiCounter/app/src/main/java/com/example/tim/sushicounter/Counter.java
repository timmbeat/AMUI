package com.example.tim.sushicounter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Counter extends AppCompatActivity {

    TextView amount; //The TextView which displays the amount of plates of food
    public static int _amount = 0; //Integer for the counting the amount of plates of food
    Button minus; //The Minus Button
    private static final String FILE_NAME = "com.example.tim.sushicounter"; //The File Name
    public static final String EXTRA_MESSAGE ="com.example.tim.EXTRA";
    public static final String AMOUNT_INSTANCE = "com.example.tim.AMOUNT";
    FileOutputStream outputStream; //outputStream for writing the file
    BufferedReader reader;
    ArrayList<String[]> datalist;
    ArrayList<String> datastats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        amount = (TextView) findViewById(R.id.amount);
        minus = (Button) findViewById(R.id.minus);
        if(_amount > 0){
            minus.setEnabled(true);
        }else {
            minus.setEnabled(false); //Set Button Activated to false. You cant have - Plates
        }
        datalist = new ArrayList<>();
        datastats = new ArrayList<>();
        File file = new File(this.getFilesDir(), FILE_NAME);
        if(!file.exists()) {
            file = new File(this.getFilesDir(), FILE_NAME);
        }
        //Load all saved messages
        if(!file.isDirectory() && file.exists()){
            loadData();
        }
        if(!checkIfToday()){
            _amount = 0;
        }
        amount.setText("" + _amount);


    }

    /**
     * This Method will count up the amount Component
     * @param view Parameter coming from plus Button component
     */
    public void countUp(View view){
        String s = String.valueOf(amount.getText());
        _amount = Integer.parseInt(s) + 1;
        if(_amount == 1){
            minus.setEnabled(true);
        }
        amount.setText(Integer.toString(_amount));
    }

    /**
     * This Method will count down the amount Component
     * @param view Parameter coming from the minus Button component
     */
    public void countDown(View view){
        String s = String.valueOf(amount.getText());
        _amount = Integer.parseInt(s) - 1;
        if(_amount == 0){
            minus.setEnabled(false);
        }
        amount.setText(Integer.toString(_amount));
    }

    /**
     * This Method will start the ShowStatistic Activity
     * @param view Parameter coming from the statistic Button component
     */
    public void toStatistics(View view) {
        Intent intent = new Intent(this, ShowStatistics.class);
        checkIfEntryExists(); //Set the newest Value
        for (String[] i: datalist
             ) {
            datastats.add("Datum => " + i[0] + " " + "Anzahl => " +  i[1]);

        }
        intent.putExtra(EXTRA_MESSAGE, datastats);
        intent.putExtra(AMOUNT_INSTANCE, _amount);
        startActivity(intent);

    }

    /**
     * When the app is paused the data will be saved
     */
    @Override
    protected void onPause(){
        super.onPause();
        //Create a date when the all you can eat was
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String _now = df.format(c.getTime());
        String[] _entry ={_now, Integer.toString(_amount)};
        if(!checkIfEntryExists()){
            datalist.add(_entry);
        }

        saveData();

    }
    /**
     * This Method will save the Data in an File in the Internal Storage
     * Created on 27.05.2017
     */
    public void saveData(){
        //Create String to read it out later
        try{
            outputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (String[] i: datalist
                 ) {
                String data = i[0] + "::::" + i[1] + "\n";
                outputStream.write(data.getBytes());

            }
            outputStream.close();

        } catch(IOException e){
            e.printStackTrace();
        } catch(IndexOutOfBoundsException e){
            try {
                outputStream.write("".getBytes());
                outputStream.close();
            } catch (IOException f) {
                e.printStackTrace();
            }
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This will load all Data from a file in the Internal Storage
     * Created on 27.05.2017
     */
    public void loadData(){
        try {

            FileInputStream input = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(input);
            reader = new BufferedReader(isr);
            String line = reader.readLine();
            while(line != null) {
                String[] entry = line.split("::::");
                datalist.add(entry);
                line = reader.readLine();
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This checks if an entry already exists in the File it will also change the entry with
     * the right amount of eaten plates
     * @return will be true if there exists a Entry, otherwise it will be false
     */
    public boolean checkIfEntryExists(){
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String _now = df.format(c.getTime());
        for (String[] i : datalist
             ) {
            if(i[0].equals(_now)){
                i[1] = Integer.toString(_amount);
                return true;
            }

        }
        return false;
    }
    public boolean checkIfToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String _now = df.format(c.getTime());

        for(String[] i : datalist
                ) {
            if(i[0].equals(_now)){
                _amount = Integer.parseInt(i[1]);
                return true;
            }
        }
        return false;
    }


    /**
     * This will safe the Instance of the Activity. In other Words the amount of plates
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(AMOUNT_INSTANCE, _amount);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This will restore the Instance of the Activity. In other Words we set the amount of plates.
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        _amount = savedInstanceState.getInt(AMOUNT_INSTANCE);

        amount.setText(Integer.toString(_amount));
    }
}
