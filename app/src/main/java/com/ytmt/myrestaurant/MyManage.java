package com.ytmt.myrestaurant;

/**
 * Created by rp06027 on 2016-03-28.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by masterUNG on 3/19/16 AD.
 */
public class MyManage {

    //Explicit
    private MyOpenHelper myOpenHelper;
    private SQLiteDatabase writeSqLiteDatabase, readSqLiteDatabase;

    public static final String user_table = "userTABLE";
    public static final String column_id = "_id";
    public static final String column_User = "User";
    public static final String column_Password = "Password";
    public static final String column_Name = "Name";


    public static final String food_table = "foodTABLE";
    public static final String column_Food = "Food";
    public static final String column_Price = "Price";
    public static final String column_Source = "Source";


    public MyManage(Context context) {

        //Create & Connected SQLite
        myOpenHelper = new MyOpenHelper(context);
        writeSqLiteDatabase = myOpenHelper.getWritableDatabase();
        readSqLiteDatabase = myOpenHelper.getReadableDatabase();

    }   // Constructor

    public String[] searchUser(String strUser) {

        try {

            String[] resultStrings = null;
            Cursor cursor = readSqLiteDatabase.query(user_table,
                    new String[]{column_id, column_User, column_Password, column_Name},
                    column_User + "=?",
                    new String[] {String.valueOf(strUser)},
                    null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    resultStrings = new String[cursor.getColumnCount()];
                    for (int i = 0; i < 4; i++) {
                        resultStrings[i] = cursor.getString(i);
                    }
                }
            }   // if
            cursor.close();
            return resultStrings;


        } catch (Exception e) {
            return null;
        }

        // return new String[0];
    }



    public long addValue(int intTABLE,
                         String strColumn2,
                         String strColumn3,
                         String strColumn4) {

        ContentValues contentValues = new ContentValues();

        long myLong = 0;

        switch (intTABLE) {
            case 1:
                // For userTABLE
                contentValues.put(column_User, strColumn2);
                contentValues.put(column_Password, strColumn3);
                contentValues.put(column_Name, strColumn4);

                myLong = writeSqLiteDatabase.insert(user_table, null, contentValues);

                break;
            case 2:
                // For foodTABLE
                contentValues.put(column_Food, strColumn2);
                contentValues.put(column_Price, strColumn3);
                contentValues.put(column_Source, strColumn4);

                myLong = writeSqLiteDatabase.insert(food_table, null, contentValues);

                break;
        }

        return myLong;
    }


}   // Main Class