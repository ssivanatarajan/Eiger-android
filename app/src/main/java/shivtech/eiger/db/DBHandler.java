package shivtech.eiger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import shivtech.eiger.models.App;
import shivtech.eiger.models.AppJSONModel;
import shivtech.eiger.models.AppPrimaryUser;
import shivtech.eiger.models.AppSecondaryUser;
import shivtech.eiger.models.Team;
import shivtech.eiger.models.Tower;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 29-01-2017.
 */

public class DBHandler extends SQLiteOpenHelper{


    public static Map<Integer,String> towerNames;
    public static Map<Integer,String> towerManagers;
    public Map<Integer,String> teamNames;
    private SQLiteDatabase db;

    private final Context myContext;
    public DBHandler(Context context){
        super(context, Constants.dbName , null, Constants.dbVersion);
        this.myContext=context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTableCreatequery="CREATE TABLE users (userID INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT NOT NULL, mobile TEXT UNIQUE, tcsEmail TEXT UNIQUE, projectEmail TEXT UNIQUE, dob DATE, empID INTEGER UNIQUE NOT NULL, bloodGroup TEXT NOT NULL,tower INTEGER NOT NULL,summary TEXT,tools TEXT,programming_langs TEXT );";

        String appsTableCreatequery="CREATE TABLE apps (appID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT NOT NULL, category TEXT CONSTRAINT categoryConstraint CHECK (category IN ('Platinum', 'Black', 'Gold', 'Silver', 'Bronze')), " +
                "supportLevel TEXT CONSTRAINT supportLevelconstraint CHECK (supportLevel IN ('L1', 'L2', 'L3')), tower INTEGER REFERENCES tower (towerID), team INTEGER REFERENCES team (teamID), alias TEXT);";

        String appPriUserMapTableCreateQuery="CREATE TABLE appprimaryusermappings (id INTEGER PRIMARY KEY  AUTOINCREMENT ,appID   REFERENCES apps (appID), empID INTEGER REFERENCES users (empID));";
        String appSecUserMapTableCreateQuery="CREATE TABLE appsecondaryusermappings (id INTEGER PRIMARY KEY  AUTOINCREMENT , appID  INTEGER REFERENCES apps (appID), empID INTEGER REFERENCES users (empID));";
        String teamTableTableCreateQuery="CREATE TABLE teams (teamID INTEGER  PRIMARY KEY AUTOINCREMENT , name TEXT NOT NULL,towerID INTEGER  REFERENCES towers (towerID));";
        String towerTableCreateQuery="CREATE TABLE towers (towerID INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT NOT NULL, manager INTEGER);";

        db.execSQL(towerTableCreateQuery);
        db.execSQL(teamTableTableCreateQuery);
        db.execSQL(userTableCreatequery);
        db.execSQL(appsTableCreatequery);
        db.execSQL(appPriUserMapTableCreateQuery);
        db.execSQL(appSecUserMapTableCreateQuery);

    }

    public void initTowers(SQLiteDatabase db){
        towerNames=new HashMap<Integer, String>();
        towerManagers=new HashMap<Integer,String>();
        //SQLiteDatabase db=this.getReadableDatabase();
try{
        String towerSelectQuery="Select "+Constants.towerId+" , "+Constants.towername +","+Constants.manager+" from "+Constants.towerTable+";";
        Cursor cursor=db.rawQuery(towerSelectQuery,null);

        if(cursor.moveToFirst())
        {
            do {

                    int towerid=cursor.getInt(0);
                    String towername=cursor.getString(1);
                    String towerManager=cursor.getString(2);
                    towerNames.put(towerid,towername);
                    towerManagers.put(towerid,towerManager);
                    Log.e("tower name",towername);
                }while (cursor.moveToNext());
            }
        //Constants.towerNames=towerNames;
        Log.e("Tower names size"," "+towerNames.size());

    }
catch(SQLException e)
{
    Log.e("init tower",e.toString());
}
        }



    public void appPrimaryUserMapping(ArrayList<AppPrimaryUser> appPrimaryUsersList)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("appprimarylist size",appPrimaryUsersList.size()+" ");
        for(int i=0;i<appPrimaryUsersList.size();i++)
        {
            AppPrimaryUser appPrimaryUser=appPrimaryUsersList.get(i);
            try
            {
                ContentValues contentValues=new ContentValues();
                contentValues.put(Constants.empId,appPrimaryUser.getEmpId());
                contentValues.put(Constants.appId,appPrimaryUser.getAppId());
                db.insert(Constants.appPrimaryUserTable,null,contentValues);
            }
            catch (SQLException ex)
            {
                Log.i("appPrimaryUserMapping",ex.toString());
            }
        }

    }

    public void appSecondaryUserMapping(ArrayList<AppSecondaryUser> appSecondaryUsersList)
    {
        SQLiteDatabase db = this.getWritableDatabase();
     Log.i("appsec list size",appSecondaryUsersList.size()+"");
        for(int i=0;i<appSecondaryUsersList.size();i++)
        {
            AppSecondaryUser appSecondaryUser=appSecondaryUsersList.get(i);
            try
            {
                ContentValues contentValues=new ContentValues();

                contentValues.put(Constants.empId,appSecondaryUser.getEmpId());
                contentValues.put(Constants.appId,appSecondaryUser.getAppId());
                db.insert(Constants.appSecondaryUserTable,null,contentValues);
            }
            catch (SQLException ex)
            {
                Log.i("appSecondaryUserMapping",ex.toString());
            }
        }

    }

    public int addUsers(ArrayList<User> usersList)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("Adding user list size",usersList.size()+"");
        for (int i = 0; i < usersList.size(); i++) {

            User user=usersList.get(i);
            try{
            ContentValues userContent=new ContentValues();
                userContent.put(Constants.userId,user.getUserId());
                userContent.put(Constants.username,user.getUserName());
                userContent.put(Constants.empId,user.getEmpID());
                userContent.put(Constants.tcsEmail,user.getTcsEmail());
                userContent.put(Constants.projectEmail,user.getProjectEmail());
                userContent.put(Constants.bloodGroup,user.getBloodGroup());
                userContent.put(Constants.dob,user.getDob());
                userContent.put(Constants.mobile,user.getUserMobile());
                userContent.put(Constants.summary,user.getSummary());
                userContent.put(Constants.tools,user.getTools());
                userContent.put(Constants.programming_langs,user.getProgramming_langs());
                userContent.put(Constants.tower,user.getUserTower());
                db.insert(Constants.userTable,null,userContent);
            }
            catch (SQLException e)
            {
                Log.i("add User",e.toString());
                return 0;
            }

        }

            return 0;
    }

    public void addTowers(ArrayList<Tower> towersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("toerlist size",towersList.size()+"");
        for (int i = 0; i < towersList.size(); i++) {

            Tower tower = towersList.get(i);
            try {
                Log.e("tower content",tower.getTowerId()+" "+tower.getTowerName()+ " "+tower.getTowerManager());
                ContentValues towerContent = new ContentValues();
                towerContent.put(Constants.towerId,tower.getTowerId());
                towerContent.put(Constants.towername,tower.getTowerName());
                towerContent.put(Constants.manager,tower.getTowerManager());

                db.insert(Constants.towerTable,null,towerContent);
            }
            catch (SQLException exp)
            {
                Log.i("add tower exp",exp.toString());
            }
        }
    }

    public void addTeams(ArrayList<Team> teamsList){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("teamlist size",teamsList.size()+"");
        for (int i = 0; i < teamsList.size(); i++) {

            Team team = teamsList.get(i);
            Log.e("team details",team.getTeamID() +" "+team.getTeamName());
            try {
                ContentValues teamContent = new ContentValues();
                teamContent.put(Constants.teamID,team.getTeamID());
                teamContent.put(Constants.teamname,team.getTeamName());
                teamContent.put(Constants.towerId,team.getTowerID());
                db.insert(Constants.teamTable,null,teamContent);
            }
            catch (SQLException exp)
            {
                Log.e("add team exp",exp.toString());
            }
        }
    }
public int getMaxAppID()
{
    SQLiteDatabase db=this.getReadableDatabase();
   Cursor c= db.rawQuery("Select MAX("+Constants.appId+") as maxAppID form "+Constants.appTable,null);
    int maxAppID=c.getInt(0);
    return  maxAppID;
}
    public void addApps(ArrayList<AppJSONModel> apps)
    {
        Log.i("addApps","adding "+apps.size() +"apps");

           SQLiteDatabase db = this.getWritableDatabase();
            initTowers(db);
           for (int i = 0; i < apps.size(); i++) {
               AppJSONModel app = apps.get(i);
               try {
                   ContentValues contentValues = new ContentValues();
                   contentValues.put(Constants.appId, app.getAppId());
                   contentValues.put(Constants.appName, app.getAppName());
                   contentValues.put(Constants.alias, app.getAppAlias());
                   contentValues.put(Constants.team, app.getAppTeamID());
                   contentValues.put(Constants.tower, app.getAppTowerID());
                   contentValues.put(Constants.category, app.getAppCategorry());
                   contentValues.put(Constants.supportLevel, app.getAppSupportLevel());
                   db.insert(Constants.appTable, null, contentValues);
            /*String insertQuery="INSERT INTO APPS("+Constants.appName+","+Constants.category+","+Constants.supportLevel+","+
                    Constants.tower+","+ Constants.team+","+Constants.alias+") VALUES('"+app.getAppName()+"' , '"+app.getAppCategorry()+"' ," +
                    " '"+app.getAppSupportLevel()+"' , '"+app.getAppTower()+"' , '"+app.getAppTeam()+"', '"+app.getAppAlias()+"');";*/
               }
               catch (SQLException exp)
               {
                   Log.i("Addapps Exp",exp.toString());
               }
               catch (Exception e)
               {
                   Log.i("Addapps Exp",e.toString());
               }
           }
       }


    public ArrayList<App> getApps(){

        ArrayList<App> appList=new ArrayList<App>();
        String selectQry="Select "+Constants.appId +", "+Constants.appName+" , "+Constants.alias+","+Constants.tower+" from " +Constants.appTable;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQry,null);
        initTowers(db);
        if(cursor.moveToFirst())
        {
            do{
                int appid= Integer.parseInt(cursor.getString(0));
                String appname=cursor.getString(1);
                String alias=cursor.getString(2);
                int appTowerid=cursor.getInt(3);
                Log.e("towerid",""+appTowerid);
                Log.e("Towername length"," "+towerNames.size());
                //Toast.makeText(myContext,appTowerid+" "+Constants.towerNames.size(),Toast.LENGTH_LONG).show();
                String appTowername=towerNames.get(appTowerid);

                Log.i("tower",appTowername);
                App app=new App(appid,appname,alias,appTowername);
                appList.add(app);


            }while (cursor.moveToNext());
        }
        return appList;
    }


    public App getAppDetails(int appId)
    {
        Log.e("appid",appId+"");
        App app=new App();
        String selectQry="Select * from apps WHERE appID= ?";
        SQLiteDatabase db=this.getReadableDatabase();
        String selectionArgs[]=new String[]{String.valueOf(appId)};
        Cursor cursor=db.rawQuery(selectQry,selectionArgs);
        cursor.moveToFirst();
        Cursor usercur=db.rawQuery("Select UserID,name,mobile from users ",null);
        Log.e("Usercur len",usercur.getCount()+"");
        Cursor primaryCur=db.rawQuery("Select "+Constants.userId +" from "+Constants.appPrimaryUserTable+" where "+Constants.appId+" = "+appId,null);
        Log.e("primaryCursor len",primaryCur.getCount()+"");
      /*  if(primaryCur.getCount()>0)
            primaryCur.moveToFirst();
            Log.e("userid",primaryCur.getInt(0)+"");

        Cursor secondaryCur=db.rawQuery("Select "+Constants.userId +" from "+Constants.appSecondaryUserTable+" where "+Constants.appId+" = "+appId,null);
        Log.e("secondaryCur len",secondaryCur.getCount()+"");
        secondaryCur.moveToFirst();
        Log.e("userid",secondaryCur.getInt(0)+"");
*/
        String primaryUserQuery="Select "+Constants.userId+","+Constants.username+","+Constants.mobile+" from "+Constants.userTable+" where "+Constants.userId +" in (" +
                "Select "+Constants.userId +" from "+Constants.appPrimaryUserTable + " where "+Constants.appId+" = "+appId+" )";

        String SecondaryUserQuery= "Select "+Constants.userId+","+Constants.username+","+Constants.mobile+" from "+Constants.userTable+" where "+Constants.userId +" in (" +
                "Select "+Constants.userId +" from "+Constants.appSecondaryUserTable + " where "+Constants.appId+" = "+appId+" )";
        //String secondarySelectionArgs[]=new String[]{String.valueOf(appId)};
        //String primarySelectionArgs[]=new String[]{String.valueOf(appId)};

        Cursor primaryUsercursor=db.rawQuery(primaryUserQuery,null);
        Cursor secondaryUsercursor=db.rawQuery(SecondaryUserQuery,null);

        if(primaryUsercursor.getCount()>0)
        {
            Log.e("primaryUSercurs len",primaryUsercursor.getCount()+"");
            primaryUsercursor.moveToFirst();
            ArrayList<User> primaryUserList=new ArrayList<User>();
            do {
                User primaryUser = new User();
                primaryUser.setUserId(primaryUsercursor.getInt(0));
                primaryUser.setUserName(primaryUsercursor.getString(1));
                primaryUser.setUserMobile(primaryUsercursor.getString(2));
                primaryUserList.add(primaryUser);
            }while (primaryUsercursor.moveToNext());
            app.setAppPrimaryRes(primaryUserList);
//String primaryUser=primaryUsercursor.getString(0);



        }
        if(secondaryUsercursor.getCount()>0)
        {
            Log.e("SecondaryUSercurs len",secondaryUsercursor.getCount()+"");
            ArrayList<User> secReslist=new ArrayList<User>();
            secondaryUsercursor.moveToFirst();
            do{
                User SecUser=new User();
                SecUser.setUserId(secondaryUsercursor.getInt(0));
                SecUser.setUserName(secondaryUsercursor.getString(1));
                SecUser.setUserMobile(secondaryUsercursor.getString(2));
                secReslist.add(SecUser);
            }while (secondaryUsercursor.moveToNext());
        app.setAppSecondaryRes(secReslist);
        }

        if(cursor.getCount()>0)
        {
            app.setAppId(cursor.getInt(cursor.getColumnIndex(Constants.appId)));
            app.setAppName(cursor.getString(cursor.getColumnIndex(Constants.appName)));
            app.setAppCategorry(cursor.getString(cursor.getColumnIndex(Constants.category)));
            app.setAppSupportLevel(cursor.getString(cursor.getColumnIndex(Constants.supportLevel)));
            int towerid=cursor.getInt(cursor.getColumnIndex(Constants.tower));
            int teamid=cursor.getInt(cursor.getColumnIndex(Constants.team));
            Log.e("team id",teamid+" ");
            String teamQuery="Select "+Constants.teamname +" from "+Constants.teamTable +" where "+Constants.teamID + " = "+teamid ;
            Cursor teamCursor=db.rawQuery(teamQuery,null);

            if(teamCursor.getCount()>0)
            {
                teamCursor.moveToFirst();
                app.setAppTeam(teamCursor.getString(0));
            }
            String towername=towerNames.get(towerid);
            String towermanager=towerManagers.get(towerid);
            app.setAppTower(towername);
            app.setTowerManager(towermanager);
            //app.setAppTeam(cursor.getInt(5));
            app.setAppAlias(cursor.getString(cursor.getColumnIndex(Constants.alias)));


        }
        return app;
    }

    public ArrayList<User> getUsers()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<User> usersList=new ArrayList<User>();
        String UserselectQuery="Select "+Constants.userId+" , "+Constants.username+" , "+Constants.mobile+" from "+Constants.userTable;
        Cursor userCursor=db.rawQuery(UserselectQuery,null);
        if(userCursor.getCount()>0)
        {
            userCursor.moveToFirst();
            do {
                String username=userCursor.getString(userCursor.getColumnIndex(Constants.username));
                int userid=userCursor.getInt(userCursor.getColumnIndex(Constants.userId));
                String useMobile=userCursor.getString(userCursor.getColumnIndex(Constants.mobile));
                User user=new User();
                user.setUserId(userid);
                user.setUserName(username);
                user.setUserMobile(useMobile);
                usersList.add(user);


            }while (userCursor.moveToNext());
        }
        return usersList;
    }

    public ArrayList<Tower> getAllTowers()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<Tower> towers=new ArrayList<Tower>();
        String all_towersqry="Select "+Constants.towerId+" , "+Constants.towername+" from "+Constants.towerTable;
        Cursor towerCursor=db.rawQuery(all_towersqry,null);
        Log.e("towers curs length",towerCursor.getCount()+"");
        if(towerCursor.getCount()>0)
        {
            towerCursor.moveToFirst();
            do{
                Tower tower=new Tower();
                tower.setTowerId(towerCursor.getInt(towerCursor.getColumnIndex(Constants.towerId)));
                tower.setTowerName(towerCursor.getString(towerCursor.getColumnIndex(Constants.towername)));
                towers.add(tower);

            }while (towerCursor.moveToNext());
        }

        return towers;
    }

    public  ArrayList<Team> getTeamsByTower(int towerid)
    {
        Log.e("towerid for teams",towerid+" ");
        SQLiteDatabase db=this.getReadableDatabase();

        ArrayList<Team> teams=new ArrayList<Team>();
        String teams_by_tower_qry="Select "+Constants.teamID+" , "+Constants.teamname+" from "+Constants.teamTable +
                " Where "+Constants.towerId+" = "+towerid;
       // String selectionArgs[]=new String[]{String.valueOf(towerid)};
        Cursor teamCursor=db.rawQuery(teams_by_tower_qry,null);
        Log.e("teamcursor count",teamCursor.getCount()+"");
        if(teamCursor.getCount()>0)
        {
            Log.e("teamCursor len",teamCursor.getCount()+"");
            teamCursor.moveToFirst();
            do{
                Team team=new Team();
                team.setTeamName(teamCursor.getString(teamCursor.getColumnIndex(Constants.towername)));
                team.setTeamID(teamCursor.getInt(teamCursor.getColumnIndex(Constants.teamID)));
                teams.add(team);


            }while (teamCursor.moveToNext());
        }

        return teams;

    }

    public ArrayList<App> getAppsbyTeams(Integer teamIDs[])
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Log.e("teamId size",teamIDs.length+" ");
        if(teamIDs.length>0)
            Log.e("teamid",teamIDs[0]+"");
        ArrayList<App> apps=new ArrayList<App>();
        String appsbyTeamQry="Select "+Constants.appId+" , "+Constants.appName+" from "+Constants.appTable +" Where "
                +Constants.team+" IN (" +makePlaceholders(teamIDs.length) + ")";
        int teamIdLength=teamIDs.length;
        String selectionargs[]=new String[teamIdLength];
        for(int i=0;i<teamIdLength;i++)
            selectionargs[i]=teamIDs[i].toString();


        Cursor appsCursor=db.rawQuery(appsbyTeamQry,selectionargs);
        Log.e("appsCursor len",appsCursor.getCount()+"");
        if(appsCursor.getCount()>0)
        {
            appsCursor.moveToFirst();
            do{
                App app=new App();
                app.setAppName(appsCursor.getString(appsCursor.getColumnIndex(Constants.appName)));
                app.setAppId(appsCursor.getInt(appsCursor.getColumnIndex(Constants.appId)));
                apps.add(app);
            }while (appsCursor.moveToNext());
        }
    return apps;
    }
    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public String getUsername(int empID)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String username_qry="Select "+Constants.username+" from "+Constants.userTable+" where "+Constants.empId +" = ?";
        String sel_args[]=new String[]{String.valueOf(empID)};
        Cursor userNameCursor=db.rawQuery(username_qry,sel_args);
        if(userNameCursor.getCount()>0)
        {
            userNameCursor.moveToFirst();
            return userNameCursor.getString(userNameCursor.getColumnIndex(Constants.username));
        }

        return "";
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
