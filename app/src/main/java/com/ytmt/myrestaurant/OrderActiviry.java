package com.ytmt.myrestaurant;

/**
 * Created by rp06027 on 2016-03-28.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    //Explicit
    private TextView showOfficerTextView;
    private Spinner deskSpinner;
    private ListView foodListView;

    private String officerString, deskString, foodString, amountString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Bind Widget
        bindWidget();

        //Show View
        showView();

        //Create Desk Spinner
        createDeskSpinner();

        //Create Food ListView
        createFoodListView();


    }   // Main Method

    private void createFoodListView() {

        //Read All SQLite
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyManage.food_table, null);
        cursor.moveToFirst();
        int intCount = cursor.getCount();

        String[] iconStrings = new String[intCount];
        final String[] foodStrings = new String[intCount];
        String[] priceStrings = new String[intCount];

        for (int i = 0; i < intCount; i++) {

            iconStrings[i] = cursor.getString(cursor.getColumnIndex(MyManage.column_Source));
            foodStrings[i] = cursor.getString(cursor.getColumnIndex(MyManage.column_Food));
            priceStrings[i] = cursor.getString(cursor.getColumnIndex(MyManage.column_Price));

            cursor.moveToNext();
        }   // for
        cursor.close();

        FoodAdapter foodAdapter = new FoodAdapter(OrderActivity.this, iconStrings,
                foodStrings, priceStrings);
        foodListView.setAdapter(foodAdapter);

        foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                foodString = foodStrings[i];

                chooseAmount();

            }   // onItemClick
        });


    }   //FoodListView

    private void chooseAmount() {

        CharSequence[] charSequences = {"1 จาน", "2 จาน", "3 จาน", "4 จาน", "5 จาน"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(foodString);
        builder.setSingleChoiceItems(charSequences, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                amountString = Integer.toString(i + 1);

                dialogInterface.dismiss();

                confirmOrder();


            }   // onClick
        });
        builder.show();

    }   // chooseAmount

    private void confirmOrder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_myaccount);
        builder.setTitle("โปรดตรวจทาน");
        builder.setMessage("Officer = " + officerString + "\n" +
                "Desk = " + deskString + "\n" +
                "Food = " + foodString + "\n" +
                "Amount = " + amountString);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateToServer();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();


    }   // confirmOrder

    private void updateToServer() {

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
            nameValuePairs.add(new BasicNameValuePair("Officer", officerString));
            nameValuePairs.add(new BasicNameValuePair("Desk", deskString));
            nameValuePairs.add(new BasicNameValuePair("Food", foodString));
            nameValuePairs.add(new BasicNameValuePair("Amount", amountString));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://swiftcodingthai.com/19Mar/php_add_order.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            httpClient.execute(httpPost);

            Toast.makeText(OrderActivity.this,
                    "Order " + foodString + " เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(OrderActivity.this,
                    "ไม่สามารถเชื่อมต่อ Server ได้", Toast.LENGTH_SHORT).show();
        }

    }   // updateToServer

    private void createDeskSpinner() {

        final String[] deskStrings = {"โต้ะ 1", "โต้ะ 2", "โต้ะ 3", "โต้ะ 4", "โต้ะ 5",
                "โต้ะ 6", "โต้ะ 7", "โต้ะ 8", "โต้ะ 9", "โต้ะ 10"};

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, deskStrings);
        deskSpinner.setAdapter(stringArrayAdapter);

        deskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deskString = deskStrings[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                deskString = deskStrings[0];
            }
        });

    }   // deskSpinner

    private void showView() {

        //Receive Value From Intent
        officerString = getIntent().getStringExtra("Officer");
        showOfficerTextView.setText(officerString);

    }

    private void bindWidget() {
        showOfficerTextView = (TextView) findViewById(R.id.textView);
        deskSpinner = (Spinner) findViewById(R.id.spinner);
        foodListView = (ListView) findViewById(R.id.listView);
    }

}   // Main Class