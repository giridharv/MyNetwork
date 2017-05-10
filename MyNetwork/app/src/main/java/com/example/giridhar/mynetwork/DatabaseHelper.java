package com.example.giridhar.mynetwork;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by giridhar on 4/3/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="USERS";
    private static final String DATABASE_TABLE_NAME="USERDETAILS";

    private static final String COLUMN_USERNAME="username";
    private static final String COLUMN_PASSWORD="password";
    private static final String COLUMN_COUNTRY="country";
    private static final String COLUMN_STATE="state";
    private static final String COLUMN_CITY="city";
    private static final String COLUMN_LOCATION_LATITUDE="latitude";
    private static final String COLUMN_LOCATION_LONGITUDE="longitude";
    private static final String COLUMN_DATEOFENTRY = "dateOfEntry";
    private static final String COLUMN_ID="id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query= " CREATE TABLE " + DATABASE_TABLE_NAME + "(" + COLUMN_USERNAME + " TEXT PRIMARY KEY, "  + COLUMN_COUNTRY + " TEXT,"+ COLUMN_STATE + " TEXT,"
                + COLUMN_CITY + " TEXT," + COLUMN_LOCATION_LATITUDE + " REAL," + COLUMN_LOCATION_LONGITUDE + " REAL,"+ COLUMN_DATEOFENTRY + " INTEGER, " + COLUMN_ID + " Integer " + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS"+DATABASE_TABLE_NAME);
        onCreate(db);
    }
    public void addPersonDetails(PersonDetails personDetails)
    {
        SQLiteDatabase database =this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COLUMN_USERNAME,personDetails.getUsername());
        contentValues.put(COLUMN_COUNTRY,personDetails.getCountry());
        contentValues.put(COLUMN_STATE,personDetails.getState());
        contentValues.put(COLUMN_CITY,personDetails.getCity());
        contentValues.put(COLUMN_LOCATION_LATITUDE,personDetails.getLatitude());
        contentValues.put(COLUMN_LOCATION_LONGITUDE,personDetails.getLongitude());
        contentValues.put(COLUMN_DATEOFENTRY,personDetails.getJoiningYear());
        contentValues.put(COLUMN_ID,personDetails.getIdForPerson());
        database.insertWithOnConflict(DATABASE_TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        database.close();
    }
    public ArrayList<PersonDetails> getPersonDetails()
    {
        ArrayList<PersonDetails> allUsers= new ArrayList<>();
        PersonDetails personDetails ;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC ",null);
        while(cursor.moveToNext())
        {
            personDetails  = new PersonDetails();
            personDetails.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
            personDetails.setCountry(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY)));
            personDetails.setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)));
            personDetails.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
            personDetails.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOCATION_LATITUDE)));
            personDetails.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            personDetails.setJoiningYear(cursor.getInt(cursor.getColumnIndex(COLUMN_DATEOFENTRY)));
            personDetails.setIdForPerson(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            allUsers.add(personDetails);

        }
        return allUsers;
    }

    public ArrayList<PersonDetails> getLimitData(int afterPos,int nextPos)
    {
        ArrayList<PersonDetails> limitedData= new ArrayList<>();
        PersonDetails personObj;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE " + COLUMN_ID + " > " + afterPos + " AND " + COLUMN_ID + " < " + nextPos + " ORDER BY " + COLUMN_ID + " DESC ",null);
        while(cursor.moveToNext())
        {
            personObj = new PersonDetails();
            personObj.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
            personObj.setCountry(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY)));
            personObj.setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)));
            personObj.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
            personObj.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOCATION_LATITUDE)));
            personObj.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            personObj.setJoiningYear(cursor.getInt(cursor.getColumnIndex(COLUMN_DATEOFENTRY)));
            personObj.setIdForPerson(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            limitedData.add(personObj);

        }
        return limitedData;
    }

    public ArrayList<PersonDetails> getFilterData(String queryValue)
    {
        ArrayList<PersonDetails> queryData= new ArrayList<>();
        PersonDetails personDetails;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryValue,null);
        while(cursor.moveToNext())
        {
            personDetails = new PersonDetails();
            personDetails.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
            personDetails.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOCATION_LATITUDE)));
            personDetails.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            personDetails.setJoiningYear(cursor.getInt(cursor.getColumnIndex(COLUMN_DATEOFENTRY)));
            personDetails.setCountry(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY)));
            personDetails.setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)));
            personDetails.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
            personDetails.setIdForPerson(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            queryData.add(personDetails);
        }
        return queryData;
    }

}
