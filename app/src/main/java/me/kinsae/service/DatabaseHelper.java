package me.kinsae.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DB_NAME = "record.db";
    public static final String TB_NAME = "tb_record";
    public static final String TB_HISTORY = "tb_history";
    public static final String TB_TEST = "tb_test";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Amount";
    public static final String COL_4 = "Remark";
    public static final String COL_5 = "Type";
    //history table columns
    public static final String HCOL_1 = "ID";
    public static final String HCOL_2 = "Ac";
    public static final String HCOL_3 = "Balance";
    public static final String HCOL_4 = "Added";
    public static final String HCOL_5 = "Subtracted";
    public static final String HCOL_6 = "Net";
    public static final String HCOL_7 = "On_Date";

    public DatabaseHelper(Context context){
        super(context, DB_NAME,null,4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TB_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT UNIQUE,Amount INTEGER,Remark TEXT,Type INTEGER)");
        db.execSQL("CREATE TABLE "+TB_HISTORY+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,AC TEXT,Balance INTEGER,Added INTEGER,Subtracted INTEGER,Net INTEGER, On_Date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TB_HISTORY+ "'", null);
        if(cursor.getCount()==0){
            db.execSQL("CREATE TABLE "+TB_HISTORY+" (ID INTEGER PRIMARY KEY AUTOINCREMENT,AC TEXT,Balance INTEGER,Added INTEGER,Subtracted INTEGER,Net INTEGER, On_Date TEXT)");
        }*/
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE "+TB_NAME+" ADD COLUMN Type INTEGER DEFAULT 0");
        }
    }
    public boolean insertData(String name, String amount,String remark,String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,amount);
        contentValues.put(COL_4,remark);
        contentValues.put(COL_5,type);
        long result = db.insert(TB_NAME,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getAccToReceive(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE Type=0 LIMIT 6",null);
        return res;
    }
    public Cursor getAccToPay(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE Type=1 LIMIT 6",null);
        return res;
    }
    public Cursor getAllAccToReceive(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE Type=0",null);
        return res;
    }
    public Cursor getAllAccToPay(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE Type=1",null);
        return res;
    }
    public Cursor getTotalToReceive(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT SUM(Amount) AS total FROM "+TB_NAME+" WHERE Type=0",null);
        return res;
    }
    public Cursor getTotalToPay(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT SUM(Amount) AS total FROM "+TB_NAME+" WHERE Type=1",null);
        return res;
    }
    public Cursor getRecCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE Type=0",null);
        return res;
    }
    public Cursor getPayCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE Type=1",null);
        return res;
    }
    public Cursor getOneData(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE "+COL_2+"='"+name+"'",null);
        return res;
    }
    public boolean updateData(String id, String amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_3,amount);
        db.update(TB_NAME, contentValues,"ID = ?",new String[]{id});
        return true;
    }
    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TB_NAME, "ID = ?",new String[]{id});
    }
    //history table CRUD
    public boolean addToHistoy(String ac, String balance,String add,String sub,String net){
        //format date
        Date date = Calendar.getInstance().getTime();
        DateFormat fday = new SimpleDateFormat("EEE");
        DateFormat fmonth = new SimpleDateFormat("d MMM");
        DateFormat fyear = new SimpleDateFormat("yyyy");
        DateFormat ftime = new SimpleDateFormat("h:mm aaa");
        String day = fday.format(date);
        String month = fmonth.format(date);
        String year = fyear.format(date);
        String time = ftime.format(date);
        String today = "["+day+"] "+month+", "+year+" at "+time;

        //insert into table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HCOL_2,ac);
        contentValues.put(HCOL_3,balance);
        contentValues.put(HCOL_4,add);
        contentValues.put(HCOL_5,sub);
        contentValues.put(HCOL_6,net);
        contentValues.put(HCOL_7,today);
        long result = db.insert(TB_HISTORY,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getHistory(String ac){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT "+TB_HISTORY+".ID,"+TB_HISTORY+".Balance,"+TB_HISTORY+".Added,"+TB_HISTORY+".Subtracted,"+TB_HISTORY+".Net,"+TB_HISTORY+".On_Date FROM "+TB_NAME+" INNER JOIN "+TB_HISTORY+" ON "+TB_NAME+".Name="+TB_HISTORY+".AC WHERE "+TB_HISTORY+".AC='"+ac+"' LIMIT 20",null);
        return res;
    }
}
