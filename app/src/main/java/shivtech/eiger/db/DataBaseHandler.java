package shivtech.eiger.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntegerRes;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import shivtech.eiger.models.App;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.*;
/**
 * Created by Sivanatarajan on 24-01-2017.
 */

public class DataBaseHandler{}
/*

public class DataBaseHandler extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/shivtech.eiger/databases/";

    private static String DB_NAME = "Eiger";

    private SQLiteDatabase myDataBase;

    private final Context myContext;
    public DataBaseHandler(Context context){
        super(context,Constants.dbName , null, Constants.dbVersion);
        this.myContext=context;
    }
/*
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
            Log.i("Dbexist","True");
        }else{
            Log.i("Dbexist","no");
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.
            Log.i("Db","Doesn't exist");

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getResources().openRawResource(shivtech.eiger.R.raw.eiger);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }
    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

      /*   String userTable_createquery="CREATE TABLE "+Constants.userTable+"("+ Constants.userId+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                 Constants._id+" TEXT ,"+ Constants.username +" TEXT,"+Constants.empId+" INTEGER,"+Constants.email+" TEXT ,"
                 +Constants.mobile+" TEXT ,"+Constants.tower+" TEXT "+")";

        String appsTable_createquery="CREATE TABLE "+Constants.appTable+"("+ Constants.appId+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                Constants._id+" TEXT ,"+ Constants.appName +" TEXT ,"+Constants.team+" TEXT  ,"+Constants.alias+" TEXT ,"
                +Constants.category+" TEXT ,"+Constants.tower+" TEXT "+")";
        db.execSQL(appsTable_createquery);
        db.execSQL(userTable_createquery);*/
/*    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
public void addApp(String appName,String team,String alias,String category,String tower)
{
    SQLiteDatabase db=this.getWritableDatabase();
    db.execSQL("INSERT INTO "+Constants.appTable+"("+Constants.appName+","+Constants.team+","+Constants.alias+","+Constants.category+","+Constants.tower+")"+ "VALUES ('"+
            appName+"','"+team+"','"+alias+"','"+category+"','"+tower+"')");
}


    public ArrayList<User> getUsers(){

        ArrayList<User> userList=new ArrayList<User>();
        String selectQry="Select "+Constants.userId +", "+Constants.username+" , "+Constants.tower+" from " +Constants.userTable;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQry,null);
        if(cursor.moveToFirst())
        {
            do{
                int userid= Integer.parseInt(cursor.getString(0));
                String username=cursor.getString(1);
                String userTower=cursor.getString(2);
                User user=new User(userid,username,userTower);
                userList.add(user);


            }while (cursor.moveToNext());
        }
        return userList;
    }
    public App getAppDetails(int appId)
    {
        App app=new App();
        String selectQry="Select * from apps WHERE appID= ?";
        SQLiteDatabase db=this.getReadableDatabase();
        String selectionArgs[]=new String[]{String.valueOf(appId)};
        Cursor cursor=db.rawQuery(selectQry,selectionArgs);
        cursor.moveToFirst();
        if(cursor!=null)
        {
                app.setAppName(cursor.getString(1));
                app.setAppAlias(cursor.getString(2));
                app.setAppCategorry(cursor.getString(3));
              //  app.setAppTeam(cursor.getInt(4));
               // app.setAppTower(cursor.getInt(5));
        }
        return app;
    }
    public ArrayList<App> getApps(){

        ArrayList<App> appList=new ArrayList<App>();
        String selectQry="Select "+Constants.appId +", "+Constants.appName+" , "+Constants.alias+","+Constants.tower+" from " +Constants.appTable;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQry,null);
        if(cursor.moveToFirst())
        {
            do{
                int appid= Integer.parseInt(cursor.getString(0));
                String appname=cursor.getString(1);
                String alias=cursor.getString(2);
                int appTower=cursor.getInt(3);
              /*  App app=new App(appid,appname,alias,appTower);
                appList.add(app);*/

/*
            }while (cursor.moveToNext());
        }
        return appList;
    }
}*/
