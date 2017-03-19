package shivtech.eiger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

public class DBHandler extends SQLiteOpenHelper {


    public static Map<Integer, String> towerNames;
    public static Map<Integer, String> towerManagers;
    private final Context myContext;
    public Map<Integer, String> teamNames;
    private SQLiteDatabase db;

    public DBHandler(Context context) {
        super(context, Constants.dbName, null, Constants.dbVersion);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTableCreatequery = "CREATE TABLE users (name TEXT NOT NULL," +
                " mobile TEXT UNIQUE, tcsEmail TEXT UNIQUE, projectEmail TEXT UNIQUE, dob DATE, empID INTEGER UNIQUE NOT NULL," +
                " bloodGroup TEXT NOT NULL,tower INTEGER NOT NULL,summary TEXT,tools TEXT,programming_langs TEXT,hobbies TEXT," +
                "isUpdate INTEGER CONSTRAINT isUpdateConstraint CHECK (isUpdate IN(0,1)) DEFAULT 0,userRole TEXT CONSTRAINT userRoleConstraint CHECK (userRole IN('U','A'))" +
                "Default 'U' ,lastModified DATE);";

        String appsTableCreatequery = "CREATE TABLE apps (appID INTEGER UNIQUE NOT NULL," +
                " name TEXT NOT NULL, category TEXT CONSTRAINT categoryConstraint CHECK (category IN ('Platinum', 'Black', 'Gold', 'Silver', 'Bronze')), " +
                "supportLevel TEXT CONSTRAINT supportLevelconstraint CHECK (supportLevel IN ('L1', 'L2', 'L3','L4')), " +
                "tower INTEGER REFERENCES tower (towerID), team INTEGER REFERENCES team (teamID), alias TEXT, " +
                "isUpdate INTEGER CONSTRAINT isUpdateConstraint CHECK (isUpdate IN(0,1)) DEFAULT 0,lastModified DATE);";

        String appPriUserMapTableCreateQuery = "CREATE TABLE appprimaryusermappings (appID   REFERENCES apps (appID), empID INTEGER REFERENCES users (empID)," +
                "isUpdate INTEGER CONSTRAINT isUpdateConstraint CHECK (isUpdate IN(0,1)) DEFAULT 0,lastModified DATE);";
        String appSecUserMapTableCreateQuery = "CREATE TABLE appsecondaryusermappings ( appID  INTEGER REFERENCES apps (appID), empID INTEGER REFERENCES users (empID)," +
                "isUpdate INTEGER CONSTRAINT isUpdateConstraint CHECK (isUpdate IN(0,1)) DEFAULT 0,lastModified DATE);";
        String teamTableTableCreateQuery = "CREATE TABLE teams (teamID INTEGER UNIQUE NOT NULL , name TEXT NOT NULL,towerID INTEGER  REFERENCES towers (towerID)," +
                "isUpdate INTEGER CONSTRAINT isUpdateConstraint CHECK (isUpdate IN(0,1)) DEFAULT 0,lastModified DATE);";
        String towerTableCreateQuery = "CREATE TABLE towers (towerID INTEGER UNIQUE NOT NULL , name TEXT NOT NULL, manager INTEGER," +
                "isUpdate INTEGER CONSTRAINT isUpdateConstraint CHECK (isUpdate IN(0,1)) DEFAULT 0,lastModified DATE);";

        db.execSQL(towerTableCreateQuery);
        db.execSQL(teamTableTableCreateQuery);
        db.execSQL(userTableCreatequery);
        db.execSQL(appsTableCreatequery);
        db.execSQL(appPriUserMapTableCreateQuery);
        db.execSQL(appSecUserMapTableCreateQuery);

    }

    public long updatePersonalProfile(String currentUser, String str_name, String str_summary, String str_tcsEmail, String str_projEmail, String str_mobile, String str_hobbies, String str_dob, String str_bloodGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        int empid = Integer.parseInt(currentUser);
        String selectUser = "Select * from " + Constants.userTable + " where " + Constants.empId + " = " + empid;
        Cursor c = db.rawQuery(selectUser, null);
        ContentValues userContentValues = new ContentValues();
        userContentValues.put(Constants.hobbies, str_hobbies);
        userContentValues.put(Constants.username, str_name);
        userContentValues.put(Constants.summary, str_summary);
        userContentValues.put(Constants.mobile, str_mobile);
        userContentValues.put(Constants.tcsEmail, str_tcsEmail);
        userContentValues.put(Constants.projectEmail, str_projEmail);
        userContentValues.put(Constants.dob, str_dob);
        userContentValues.put(Constants.bloodGroup, str_bloodGroup);

        if (c.getCount() > 0) {
            String selection = Constants.empId + " = ?";
            String[] selectionArgs = {"" + currentUser};
            c.close();
            return db.update(Constants.userTable, userContentValues, selection, selectionArgs);
        } else {
            userContentValues.put(Constants.empId, empid);
            long res = db.insert(Constants.userTable, null, userContentValues);
            c.close();
            return res;

        }

    }

    public String getLastModifiedDate(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select MAX(lastModified) from " + table;
        Log.e("get lastmod table", table);
        Cursor cursor = db.rawQuery(query, null);

        Log.e("cursor count", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String lastModified = cursor.getString(0);

            Log.e("lastmodified", lastModified);
            //cursor.close();
            return lastModified;
        }
        return "";
    }

    public void setDirtytoDefault(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs[] = new String[]{String.valueOf(1)};
        ContentValues dirtyContentValues = new ContentValues();
        dirtyContentValues.put(Constants.isupdate, 0);
        String whereClause = Constants.isupdate + " = ? ";
        db.update(table, dirtyContentValues, whereClause, whereArgs);

    }

    public void addOrUpdateApps(ArrayList<AppJSONModel> appJSONModels) {
        SQLiteDatabase db = this.getWritableDatabase();
        //initTowers(db);
        Log.e("addorupdateapps length", appJSONModels.size() + "");
        for (int i = 0; i < appJSONModels.size(); i++) {
            AppJSONModel app = appJSONModels.get(i);
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.appId, app.getAppId());
                contentValues.put(Constants.appName, app.getAppName());
                contentValues.put(Constants.alias, app.getAppAlias());
                contentValues.put(Constants.team, app.getAppTeamID());
                contentValues.put(Constants.tower, app.getAppTowerID());
                contentValues.put(Constants.category, app.getAppCategorry());
                contentValues.put(Constants.supportLevel, app.getAppSupportLevel());
                contentValues.put(Constants.lastModified, app.getLastModified());
                Log.e("addupdate-appdetails", app.getAppName() + ' ' + app.getAppId() + "" + app.getAppAlias());
                long res = db.insertWithOnConflict(Constants.appTable, Constants.appId, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.e("addorupdaters", "" + res);
                /*if(res==-1)
                {
                    Log.e("updating app",app.getAppId()+"");
                    contentValues.remove(Constants.appId);
                    String[] whereArgs = { "" + app.getAppId() };
                    String whereClause=Constants.appId+" = ?";
                    db.update(Constants.appTable,contentValues,whereClause,whereArgs);
                }*/
            } catch (SQLException exp) {
                Log.e("Addapps Exp", exp.toString());
            } catch (Exception e) {
                Log.i("addorupdate Exp", e.toString());
            }
        }
    }

    public void addOrUpdateUsers(ArrayList<User> usersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("Adding user list size", usersList.size() + "");
        for (int i = 0; i < usersList.size(); i++) {

            User user = usersList.get(i);
            try {
                ContentValues userContent = new ContentValues();
                //userContent.put(Constants.empId,user.getEmpID());
                userContent.put(Constants.username, user.getUserName());
                userContent.put(Constants.empId, user.getEmpID());
                userContent.put(Constants.tcsEmail, user.getTcsEmail());
                userContent.put(Constants.projectEmail, user.getProjectEmail());
                userContent.put(Constants.bloodGroup, user.getBloodGroup());
                userContent.put(Constants.dob, user.getDob());
                userContent.put(Constants.mobile, user.getUserMobile());
                userContent.put(Constants.summary, user.getSummary());
                userContent.put(Constants.tools, user.getTools());
                userContent.put(Constants.programming_langs, user.getProgramming_langs());
                userContent.put(Constants.hobbies, user.getHobbies());
                userContent.put(Constants.tower, user.getUserTowerID());
                userContent.put(Constants.lastModified, user.getLastModified());
                userContent.put(Constants.userRole, user.getUserRole());
                long res = db.insertWithOnConflict(Constants.userTable, Constants.empId, userContent, SQLiteDatabase.CONFLICT_REPLACE);
               /* if(res==-1)
                {
                    Log.e("updating user",user.getEmpID()+"");
                    userContent.remove(Constants.empId);
                    String[] whereArgs = { "" + user.getEmpID() };
                    String whereClause=Constants.empId+" = ?";
                    db.update(Constants.userTable,userContent,whereClause,whereArgs);
                }*/
            } catch (SQLException e) {
                Log.e("addorupdate User exp", e.toString());

            }

        }
    }

    public void addOrUpdateTeams(ArrayList<Team> teamsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("teamlist size", teamsList.size() + "");
        for (int i = 0; i < teamsList.size(); i++) {

            Team team = teamsList.get(i);
            Log.e("team details", team.getTeamID() + " " + team.getTeamName());
            try {
                ContentValues teamContent = new ContentValues();
                teamContent.put(Constants.teamID, team.getTeamID());
                teamContent.put(Constants.teamname, team.getTeamName());
                teamContent.put(Constants.towerId, team.getTowerID());
                teamContent.put(Constants.lastModified, team.getLastModified());
                long res = db.insertWithOnConflict(Constants.teamTable, Constants.teamID, teamContent, SQLiteDatabase.CONFLICT_REPLACE);
                Log.e("res", res + "");
              /*  if(res==-1)
                {
                    teamContent.remove(Constants.teamID);
                    String[] whereArgs = { "" + team.getTeamID() };
                    String whereClause=Constants.teamID+" = ?";

                    db.update(Constants.teamTable,teamContent,whereClause,whereArgs);
                }*/
            } catch (SQLException exp) {
                Log.e("addorupdate team exp", exp.toString());
            }
        }
    }

    public void addOrUpdateTower(ArrayList<Tower> towersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("towerlist size", towersList.size() + "");
        for (int i = 0; i < towersList.size(); i++) {

            Tower tower = towersList.get(i);
            try {
                Log.e("tower content", tower.getTowerId() + " " + tower.getTowerName() + " " + tower.getTowerManager());
                ContentValues towerContent = new ContentValues();
                towerContent.put(Constants.towerId, tower.getTowerId());
                towerContent.put(Constants.towername, tower.getTowerName());
                towerContent.put(Constants.manager, tower.getTowerManager());
                towerContent.put(Constants.lastModified, tower.getLastModified());
                long res = db.insertWithOnConflict(Constants.towerTable, Constants.towerId, towerContent, SQLiteDatabase.CONFLICT_REPLACE);
                Log.e("res", res + "");
                /*if(res==-1)
                {
                    towerContent.remove(Constants.towerId);
                    String[] whereArgs = { "" + tower.getTowerId() };
                    String whereClause=Constants.towerId+" = ?";
                    db.update(Constants.towerTable,towerContent,whereClause,whereArgs);
                }*/
            } catch (SQLException exp) {
                Log.i("addorupdate tower exp", exp.toString());
            }
        }
    }

    public void addOrUpdateAppPrimarymapping(ArrayList<AppPrimaryUser> appPrimaryUsers) {
        SQLiteDatabase db = this.getWritableDatabase();
        Set<Integer> UniqueEmpIDS = new HashSet<>();
        int appPrilistSize = appPrimaryUsers.size();

        for (int i = 0; i < appPrilistSize; i++)
            UniqueEmpIDS.add(appPrimaryUsers.get(i).getEmpId());
       /* String[] empids=new String[UniqueEmpIDS.size()];
        for(int i=0;i<UniqueEmpIDS.size();i++)
        {
            empids[i]= String.valueOf(UniqueEmpIDS.);
        }*/
        String empids[] = Arrays.copyOf(UniqueEmpIDS.toArray(), UniqueEmpIDS.size(), String[].class);
       /* String teamsQuery = "select distinct " + Constants.teamname +" , "+Constants.teamID +" from  " + Constants.teamTable + " where  " + Constants.teamID
                + " IN (" + makePlaceholders(empids.length) + ")";*/
        //String empSelArgs[] = getStringArray(empids);
        String args = TextUtils.join(", ", empids);
        String query = "DELETE FROM  " + Constants.appPrimaryUserTable + " WHERE " + Constants.empId + " IN (%s)";
        db.execSQL(String.format(query, args));
        for (int i = 0; i < appPrilistSize; i++) {
            AppPrimaryUser appPrimaryUser = appPrimaryUsers.get(i);
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.empId, appPrimaryUser.getEmpId());
                contentValues.put(Constants.appId, appPrimaryUser.getAppId());
                contentValues.put(Constants.lastModified, appPrimaryUser.getLastModified());
                db.insert(Constants.appPrimaryUserTable, null, contentValues);
            } catch (SQLException ex) {
                Log.e("appPriUseraddorupdate", ex.toString());
            }
        }

    }

    public void addOrUpdateAppSecondaryUsermapping(ArrayList<AppSecondaryUser> appSecondaryUsers) {
        SQLiteDatabase db = this.getWritableDatabase();
        Set<Integer> UniqueEmpIDS = new HashSet<>();
        int appseclistSize = appSecondaryUsers.size();
        for (int i = 0; i < appseclistSize; i++)
            UniqueEmpIDS.add(appSecondaryUsers.get(i).getEmpId());

        String empids[] = Arrays.copyOf(UniqueEmpIDS.toArray(), UniqueEmpIDS.size(), String[].class);
       /* String teamsQuery = "select distinct " + Constants.teamname +" , "+Constants.teamID +" from  " + Constants.teamTable + " where  " + Constants.teamID
                + " IN (" + makePlaceholders(empids.length) + ")";*/
        //String empSelArgs[] = getStringArray(empids);
        String args = TextUtils.join(", ", empids);
        String query = "DELETE FROM " + Constants.appSecondaryUserTable + " WHERE " + Constants.empId + " IN (%s)";
        db.execSQL(String.format(query, args));
        for (int i = 0; i < appseclistSize; i++) {
            AppSecondaryUser appSecondaryUser = appSecondaryUsers.get(i);
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.empId, appSecondaryUser.getEmpId());
                contentValues.put(Constants.appId, appSecondaryUser.getAppId());
                contentValues.put(Constants.lastModified, appSecondaryUser.getLastModified());
                db.insert(Constants.appSecondaryUserTable, null, contentValues);
            } catch (SQLException ex) {
                Log.e("appsecUseraddorupdate", ex.toString());
            }
        }

    }

    public void deleteAppIDs(ArrayList<Integer> appIDsList) {
        Log.e("DeleteAppids", "in");
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + Constants.appId + " from " + Constants.appTable;
        ArrayList<Integer> dbappIDslist = new ArrayList<Integer>();
        Cursor appIDCursor = db.rawQuery(query, null);
        Log.e("delappidcurslength", appIDCursor.getCount() + "");
        if (appIDCursor.getCount() > 0) {
            appIDCursor.moveToFirst();
            do {

                dbappIDslist.add(appIDCursor.getInt(appIDCursor.getColumnIndex(Constants.appId)));
            } while (appIDCursor.moveToNext());
        }
        Collection<Integer> appIDCollection = new ArrayList(appIDsList);

        dbappIDslist.removeAll(appIDCollection);
        String[] delAppIDS = new String[dbappIDslist.size()];
        for (int i = 0; i < dbappIDslist.size(); i++) {
            delAppIDS[i] = String.valueOf(dbappIDslist.get(i));
        }
        //String[] delAppIDS=Arrays.copyOf(dbappIDslist.toArray(),dbappIDslist.size(),String[].class);
        Log.e("Delete app ids", dbappIDslist.size() + "");

        String args = TextUtils.join(", ", delAppIDS);
        String delAPPQuery = "DELETE FROM " + Constants.appTable + " WHERE " + Constants.appId + " IN (%s)";
        db.execSQL(String.format(delAPPQuery, args));
        String delPrimaryAPPSQuery = "DELETE FROM " + Constants.appPrimaryUserTable + " WHERE " + Constants.appId + " IN (%s)";
        db.execSQL(String.format(delPrimaryAPPSQuery, args));
        String delSecondaryAPPSQuery = "DELETE FROM " + Constants.appSecondaryUserTable + " WHERE " + Constants.appId + " IN (%s)";
        db.execSQL(String.format(delSecondaryAPPSQuery, args));

    }

    public void deleteEmpIDs(ArrayList<Integer> EmpIDsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + Constants.empId + " from " + Constants.userTable;
        ArrayList<Integer> dbEmpIDslist = new ArrayList<Integer>();
        Cursor empIDCursor = db.rawQuery(query, null);
        Log.e("empid curs length", empIDCursor.getCount() + "");
        if (empIDCursor.getCount() > 0) {
            empIDCursor.moveToFirst();
            do {

                int empid = empIDCursor.getInt(empIDCursor.getColumnIndex(Constants.empId));
                Log.e("del empid", empid + "");
                dbEmpIDslist.add(empid);
            } while (empIDCursor.moveToNext());
        }
        Collection<Integer> empIDCollection = new ArrayList(EmpIDsList);

        dbEmpIDslist.removeAll(empIDCollection);
        String[] delempIDS = new String[dbEmpIDslist.size()];
        for (int i = 0; i < dbEmpIDslist.size(); i++) {
            delempIDS[i] = String.valueOf(dbEmpIDslist.get(i));
        }

        //String[] delempIDS=Arrays.copyOf(dbEmpIDslist.toArray(),dbEmpIDslist.size(),String[].class);
        Log.e("Delete emp ids", dbEmpIDslist.size() + "");
        String args = TextUtils.join(", ", delempIDS);
        String delAPPQuery = "DELETE FROM " + Constants.userTable + " WHERE " + Constants.empId + " IN (%s)";
        db.execSQL(String.format(delAPPQuery, args));
        String delPrimaryAPPSQuery = "DELETE FROM " + Constants.appPrimaryUserTable + " WHERE " + Constants.empId + " IN (%s)";
        db.execSQL(String.format(delPrimaryAPPSQuery, args));
        String delSecondaryAPPSQuery = "DELETE FROM " + Constants.appSecondaryUserTable + " WHERE " + Constants.empId + " IN (%s)";
        db.execSQL(String.format(delSecondaryAPPSQuery, args));

    }

    public void deleteTeamIDs(ArrayList<Integer> TeamIDsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + Constants.teamID + " from " + Constants.teamTable;
        ArrayList<Integer> dbteamIDslist = new ArrayList<Integer>();
        Cursor teamIDCursor = db.rawQuery(query, null);
        if (teamIDCursor.getCount() > 0) {
            teamIDCursor.moveToFirst();
            do {

                dbteamIDslist.add(teamIDCursor.getInt(teamIDCursor.getColumnIndex(Constants.teamID)));
            } while (teamIDCursor.moveToNext());
        }
        Collection<Integer> teamIDCollection = new ArrayList(TeamIDsList);

        dbteamIDslist.removeAll(teamIDCollection);
        String[] delteamIDS = new String[dbteamIDslist.size()];
        for (int i = 0; i < dbteamIDslist.size(); i++) {
            delteamIDS[i] = String.valueOf(dbteamIDslist.get(i));
        }

        // String[] delteamIDS=Arrays.copyOf(dbteamIDslist.toArray(),dbteamIDslist.size(),String[].class);
        Log.e("Delete team ids", dbteamIDslist.size() + "");
        String args = TextUtils.join(", ", delteamIDS);
        String delTeamQuery = "DELETE FROM " + Constants.teamTable + " WHERE " + Constants.teamID + " IN (%s)";
        db.execSQL(String.format(delTeamQuery, args));

    }

    public void deleteTowerIDs(ArrayList<Integer> TowerIDsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select " + Constants.towerId + " from " + Constants.towerTable;
        ArrayList<Integer> dbtowerIDslist = new ArrayList<Integer>();
        Cursor towerIDCursor = db.rawQuery(query, null);
        if (towerIDCursor.getCount() > 0) {
            towerIDCursor.moveToFirst();
            do {

                dbtowerIDslist.add(towerIDCursor.getInt(towerIDCursor.getColumnIndex(Constants.towerId)));
            } while (towerIDCursor.moveToNext());
        }
        Collection<Integer> teamIDCollection = new ArrayList(TowerIDsList);

        dbtowerIDslist.removeAll(teamIDCollection);
        String[] deltowerIDS = new String[dbtowerIDslist.size()];
        for (int i = 0; i < dbtowerIDslist.size(); i++) {
            deltowerIDS[i] = String.valueOf(dbtowerIDslist.get(i));
        }
        //String[] deltowerIDS=Arrays.copyOf(dbtowerIDslist.toArray(),dbtowerIDslist.size(),String[].class);
        Log.e("Delete tower ids", dbtowerIDslist.size() + "");
        String args = TextUtils.join(", ", deltowerIDS);
        String delTowerQuery = "DELETE FROM " + Constants.towerTable + " WHERE " + Constants.towerId + " IN (%s)";
        db.execSQL(String.format(delTowerQuery, args));

    }


    public void makeDirty(String table, int value, String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {"" + where};
        String whereClause = null;
        if (table.equals(Constants.userTable) || table.equals(Constants.appPrimaryUserTable) ||
                table.equals(Constants.appSecondaryUserTable)) {
            whereClause = Constants.empId + " = ?";
        } else if (table.equals(Constants.appTable)) {
            whereClause = Constants.appId + " = ?";
        } else if (table.equals(Constants.towerTable)) {
            whereClause = Constants.towerId + " = ?";
        } else if (table.equals(Constants.teamTable))
            whereClause = Constants.teamID + " = ?";
        ContentValues dirtyContentValues = new ContentValues();
        dirtyContentValues.put(Constants.isupdate, value);
        int res = db.update(table, dirtyContentValues, whereClause, whereArgs);
        Log.e("make dirty res", res + "");
    }

    public void initTowers(SQLiteDatabase db) {
        towerNames = new HashMap<Integer, String>();
        towerManagers = new HashMap<Integer, String>();
        //SQLiteDatabase db=this.getReadableDatabase();
        try {
            String towerSelectQuery = "Select " + Constants.towerId + " , " + Constants.towername + " , " + Constants.manager + " from " + Constants.towerTable + ";";
            Cursor cursor = db.rawQuery(towerSelectQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    int towerid = cursor.getInt(0);
                    String towername = cursor.getString(1);
                    int towerManagerID = cursor.getInt(2);
                    towerNames.put(towerid, towername);
                    String towerManager = getUsername(towerManagerID);
                    towerManagers.put(towerid, towerManager);
                    Log.e("tower name", towername + " " + towerManager);
                } while (cursor.moveToNext());
            }
            //Constants.towerNames=towerNames;
            Log.e("Tower names size", " " + towerNames.size());
            cursor.close();
        } catch (SQLException e) {
            Log.e("init tower", e.toString());

        }
    }


    public void appPrimaryUserMapping(ArrayList<AppPrimaryUser> appPrimaryUsersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("appprimarylist size", appPrimaryUsersList.size() + " ");
        for (int i = 0; i < appPrimaryUsersList.size(); i++) {
            AppPrimaryUser appPrimaryUser = appPrimaryUsersList.get(i);
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.empId, appPrimaryUser.getEmpId());
                contentValues.put(Constants.appId, appPrimaryUser.getAppId());
                contentValues.put(Constants.lastModified, appPrimaryUser.getLastModified());
                db.insert(Constants.appPrimaryUserTable, null, contentValues);
            } catch (SQLException ex) {
                Log.i("appPrimaryUserMapping", ex.toString());
            }
        }

    }

    public void appSecondaryUserMapping(ArrayList<AppSecondaryUser> appSecondaryUsersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("appsec list size", appSecondaryUsersList.size() + "");
        for (int i = 0; i < appSecondaryUsersList.size(); i++) {
            AppSecondaryUser appSecondaryUser = appSecondaryUsersList.get(i);
            try {
                ContentValues contentValues = new ContentValues();

                contentValues.put(Constants.empId, appSecondaryUser.getEmpId());
                contentValues.put(Constants.appId, appSecondaryUser.getAppId());
                contentValues.put(Constants.lastModified, appSecondaryUser.getLastModified());
                db.insert(Constants.appSecondaryUserTable, null, contentValues);
            } catch (SQLException ex) {
                Log.i("appSecondaryUserMapping", ex.toString());
            }
        }

    }

    public int addUsers(ArrayList<User> usersList) {

        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("Adding user list size", usersList.size() + "");
        for (int i = 0; i < usersList.size(); i++) {

            User user = usersList.get(i);
            try {
                ContentValues userContent = new ContentValues();
                //userContent.put(Constants.empId,user.getEmpID());
                userContent.put(Constants.username, user.getUserName());
                userContent.put(Constants.empId, user.getEmpID());
                userContent.put(Constants.tcsEmail, user.getTcsEmail());
                userContent.put(Constants.projectEmail, user.getProjectEmail());
                userContent.put(Constants.bloodGroup, user.getBloodGroup());
                userContent.put(Constants.dob, user.getDob());
                userContent.put(Constants.mobile, user.getUserMobile());
                userContent.put(Constants.summary, user.getSummary());
                userContent.put(Constants.tools, user.getTools());
                userContent.put(Constants.programming_langs, user.getProgramming_langs());
                userContent.put(Constants.hobbies, user.getHobbies());
                userContent.put(Constants.tower, user.getUserTowerID());
                userContent.put(Constants.lastModified, user.getLastModified());
                userContent.put(Constants.userRole, user.getUserRole());
                db.insert(Constants.userTable, null, userContent);
            } catch (SQLException e) {
                Log.e("add User exp", e.toString());
                return 0;
            }

        }

        return 0;
    }

    public void addTowers(ArrayList<Tower> towersList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("toerlist size", towersList.size() + "");
        for (int i = 0; i < towersList.size(); i++) {

            Tower tower = towersList.get(i);
            try {
                Log.e("tower content", tower.getTowerId() + " " + tower.getTowerName() + " " + tower.getTowerManager());
                ContentValues towerContent = new ContentValues();
                towerContent.put(Constants.towerId, tower.getTowerId());
                towerContent.put(Constants.towername, tower.getTowerName());
                towerContent.put(Constants.manager, tower.getTowerManager());
                towerContent.put(Constants.lastModified, tower.getLastModified());
                db.insert(Constants.towerTable, null, towerContent);
            } catch (SQLException exp) {
                Log.i("add tower exp", exp.toString());
            }
        }
    }

    public void addTeams(ArrayList<Team> teamsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("teamlist size", teamsList.size() + "");
        for (int i = 0; i < teamsList.size(); i++) {

            Team team = teamsList.get(i);
            Log.e("team details", team.getTeamID() + " " + team.getTeamName());
            try {
                ContentValues teamContent = new ContentValues();
                teamContent.put(Constants.teamID, team.getTeamID());
                teamContent.put(Constants.teamname, team.getTeamName());
                teamContent.put(Constants.towerId, team.getTowerID());
                teamContent.put(Constants.lastModified, team.getLastModified());
                db.insert(Constants.teamTable, null, teamContent);
            } catch (SQLException exp) {
                Log.e("add team exp", exp.toString());
            }
        }
    }

    public int getMaxAppID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("Select MAX(" + Constants.appId + ") as maxAppID form " + Constants.appTable, null);
        int maxAppID = c.getInt(0);
        return maxAppID;
    }

    public void addApps(ArrayList<AppJSONModel> apps) {
        Log.i("addApps", "adding " + apps.size() + "apps");

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
                contentValues.put(Constants.lastModified, app.getLastModified());
                db.insert(Constants.appTable, null, contentValues);
            /*String insertQuery="INSERT INTO APPS("+Constants.appName+","+Constants.category+","+Constants.supportLevel+","+
                    Constants.tower+","+ Constants.team+","+Constants.alias+") VALUES('"+app.getAppName()+"' , '"+app.getAppCategorry()+"' ," +
                    " '"+app.getAppSupportLevel()+"' , '"+app.getAppTower()+"' , '"+app.getAppTeam()+"', '"+app.getAppAlias()+"');";*/
            } catch (SQLException exp) {
                Log.i("Addapps Exp", exp.toString());
            } catch (Exception e) {
                Log.i("Addapps Exp", e.toString());
            }
        }
    }


    public ArrayList<App> getApps() {

        ArrayList<App> appList = new ArrayList<App>();
        String selectQry = "Select " + Constants.appId + ", " + Constants.appName + " , " + Constants.alias + "," + Constants.tower + " from " + Constants.appTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQry, null);
        initTowers(db);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                int appid = Integer.parseInt(cursor.getString(0));
                String appname = cursor.getString(1);
                String alias = cursor.getString(2);
                int appTowerid = cursor.getInt(3);
                Log.e("towerid", "" + appTowerid);
                Log.e("Towername length", " " + towerNames.size());
                //Toast.makeText(myContext,appTowerid+" "+Constants.towerNames.size(),Toast.LENGTH_LONG).show();
                String appTowername = towerNames.get(appTowerid);

                Log.i("tower", appTowername);
                App app = new App(appid, appname, alias, appTowername);
                appList.add(app);


            } while (cursor.moveToNext());
        }
        return appList;
    }


    public App getAppDetails(int appId) {
        Log.e("appid", appId + "");
        App app = new App();
        String selectQry = "Select * from apps WHERE appID= ?";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectionArgs[] = new String[]{String.valueOf(appId)};
        Cursor cursor = db.rawQuery(selectQry, selectionArgs);
        cursor.moveToFirst();
        Cursor primaryCur = db.rawQuery("Select " + Constants.empId + " from " + Constants.appPrimaryUserTable + " where " + Constants.appId + " = " + appId, null);
        Log.e("primaryCursor len", primaryCur.getCount() + "");
      /*  if(primaryCur.getCount()>0)
            primaryCur.moveToFirst();
            Log.e("userid",primaryCur.getInt(0)+"");

        Cursor secondaryCur=db.rawQuery("Select "+Constants.userId +" from "+Constants.appSecondaryUserTable+" where "+Constants.appId+" = "+appId,null);
        Log.e("secondaryCur len",secondaryCur.getCount()+"");
        secondaryCur.moveToFirst();
        Log.e("userid",secondaryCur.getInt(0)+"");
*/
        String primaryUserQuery = "Select " + Constants.empId + "," + Constants.username + "," + Constants.mobile + " from " + Constants.userTable + " where " + Constants.empId + " in (" +
                "Select " + Constants.empId + " from " + Constants.appPrimaryUserTable + " where " + Constants.appId + " = " + appId + " )";

        String SecondaryUserQuery = "Select " + Constants.empId + "," + Constants.username + "," + Constants.mobile + " from " + Constants.userTable + " where " + Constants.empId + " in (" +
                "Select " + Constants.empId + " from " + Constants.appSecondaryUserTable + " where " + Constants.appId + " = " + appId + " )";
        //String secondarySelectionArgs[]=new String[]{String.valueOf(appId)};
        //String primarySelectionArgs[]=new String[]{String.valueOf(appId)};

        Cursor primaryUsercursor = db.rawQuery(primaryUserQuery, null);
        Cursor secondaryUsercursor = db.rawQuery(SecondaryUserQuery, null);
        ArrayList<User> primaryUserList = new ArrayList<User>();
        if (primaryUsercursor.getCount() > 0) {
            Log.e("primaryUSercurs len", primaryUsercursor.getCount() + "");
            primaryUsercursor.moveToFirst();

            do {
                User primaryUser = new User();
                primaryUser.setEmpID(primaryUsercursor.getInt(0));
                primaryUser.setUserName(primaryUsercursor.getString(1));
                primaryUser.setUserMobile(primaryUsercursor.getString(2));
                primaryUserList.add(primaryUser);
            } while (primaryUsercursor.moveToNext());

//String primaryUser=primaryUsercursor.getString(0);

        }
        app.setAppPrimaryRes(primaryUserList);
        ArrayList<User> secReslist = new ArrayList<User>();
        if (secondaryUsercursor.getCount() > 0) {
            Log.e("SecondaryUSercurs len", secondaryUsercursor.getCount() + "");

            secondaryUsercursor.moveToFirst();
            do {
                User SecUser = new User();
                SecUser.setEmpID(secondaryUsercursor.getInt(0));
                SecUser.setUserName(secondaryUsercursor.getString(1));
                SecUser.setUserMobile(secondaryUsercursor.getString(2));
                secReslist.add(SecUser);
            } while (secondaryUsercursor.moveToNext());

        }
        app.setAppSecondaryRes(secReslist);
        if (cursor.getCount() > 0) {
            app.setAppId(cursor.getInt(cursor.getColumnIndex(Constants.appId)));
            app.setAppName(cursor.getString(cursor.getColumnIndex(Constants.appName)));
            app.setAppCategorry(cursor.getString(cursor.getColumnIndex(Constants.category)));
            app.setAppSupportLevel(cursor.getString(cursor.getColumnIndex(Constants.supportLevel)));
            int towerid = cursor.getInt(cursor.getColumnIndex(Constants.tower));
            int teamid = cursor.getInt(cursor.getColumnIndex(Constants.team));
            Log.e("team id", teamid + " ");
            String teamQuery = "Select " + Constants.teamname + " from " + Constants.teamTable + " where " + Constants.teamID + " = " + teamid;
            Cursor teamCursor = db.rawQuery(teamQuery, null);

            if (teamCursor.getCount() > 0) {
                teamCursor.moveToFirst();
                app.setAppTeam(teamCursor.getString(0));
            }
            String towername = towerNames.get(towerid);
            String towermanager = towerManagers.get(towerid);
            app.setAppTower(towername);
            app.setTowerManager(towermanager);
            //app.setAppTeam(cursor.getInt(5));
            app.setAppAlias(cursor.getString(cursor.getColumnIndex(Constants.alias)));


        }
        return app;
    }

    public Cursor getDirtyTableValues(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + table + " where " + Constants.isupdate + " = " + 1;

        return db.rawQuery(Query, null);
    }


    public ArrayList<User> getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<User> usersList = new ArrayList<User>();
        String UserselectQuery = "Select " + Constants.empId + " , " + Constants.username + " , " + Constants.mobile + " from " + Constants.userTable +
                " order by " + Constants.username;
        Cursor userCursor = db.rawQuery(UserselectQuery, null);
        if (userCursor.getCount() > 0) {
            userCursor.moveToFirst();
            do {
                String username = userCursor.getString(userCursor.getColumnIndex(Constants.username));
                int empid = userCursor.getInt(userCursor.getColumnIndex(Constants.empId));
                String useMobile = userCursor.getString(userCursor.getColumnIndex(Constants.mobile));
                User user = new User();
                user.setEmpID(empid);
                user.setUserName(username);
                user.setUserMobile(useMobile);
                usersList.add(user);


            } while (userCursor.moveToNext());
        }
        return usersList;
    }

    public User getUserdetails(int empid) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        String UserselectQuery = "Select * from " + Constants.userTable + " where " + Constants.empId + " = ?";
        String userSelArgs[] = new String[]{String.valueOf(empid)};
        Cursor userCursor = db.rawQuery(UserselectQuery, userSelArgs);
        if (userCursor.getCount() > 0) {
            userCursor.moveToFirst();
            user.setUserName(userCursor.getString(userCursor.getColumnIndex(Constants.username)));
            user.setUserMobile(userCursor.getString(userCursor.getColumnIndex(Constants.mobile)));
            user.setBloodGroup(userCursor.getString(userCursor.getColumnIndex(Constants.bloodGroup)));
            user.setProgramming_langs(userCursor.getString(userCursor.getColumnIndex(Constants.programming_langs)));
            user.setTools(userCursor.getString(userCursor.getColumnIndex(Constants.tools)));
            user.setDob(userCursor.getString(userCursor.getColumnIndex(Constants.dob)));
            //int empid=userCursor.getInt(userCursor.getColumnIndex(Constants.empId));
            user.setEmpID(empid);
            user.setProjectEmail(userCursor.getString(userCursor.getColumnIndex(Constants.projectEmail)));
            user.setTcsEmail(userCursor.getString(userCursor.getColumnIndex(Constants.tcsEmail)));
            user.setSummary(userCursor.getString(userCursor.getColumnIndex(Constants.summary)));
            user.setHobbies(userCursor.getString(userCursor.getColumnIndex(Constants.hobbies)));
            user.setUserRole(userCursor.getString(userCursor.getColumnIndex(Constants.userRole)));
            initTowers(db);
            int towerID = userCursor.getInt(userCursor.getColumnIndex(Constants.tower));
            String towername = towerNames.get(towerID);
            Log.e("user towername manager", towerID + " " + towername + " " + towerManagers.get(towerID));
            user.setUserTowerName(towername);
            user.setTowerManager(towerManagers.get(towerID));
            ArrayList<App> primaryAppslist = getAppsbyUser(empid, 0);//0 -  primary apps
            ArrayList<App> secAppsList = getAppsbyUser(empid, 1);//1 - secondary apps;
            user.setPrimaryApps(primaryAppslist);//0 -  primary apps
            user.setSecondaryApps(secAppsList); //1 - secondary apps;
            Integer teamIDs[] = new Integer[primaryAppslist.size() + secAppsList.size()];

            //first insert primary app's teamIDs to array then insert secapps' teamIDs
            int teamIds_len = teamIDs.length;
            int primaryappIDs_len = primaryAppslist.size();
            Log.e("primary apps len", primaryappIDs_len + "");
            for (int i = 0; i < primaryAppslist.size(); i++)
                teamIDs[i] = primaryAppslist.get(i).getTeamId();
            for (int i = primaryappIDs_len; i < teamIds_len; i++)
                teamIDs[i] = secAppsList.get(i - primaryappIDs_len).getTeamId();
            if (teamIDs.length > 0) {
                String teamsQuery = "select distinct " + Constants.teamname + " , " + Constants.teamID + " from  " + Constants.teamTable + " where  " + Constants.teamID
                        + " IN (" + makePlaceholders(teamIDs.length) + ")";
                String teamSelArgs[] = getStringArray(teamIDs);
                Cursor teamCursor = db.rawQuery(teamsQuery, teamSelArgs);

                ArrayList<Team> teams = new ArrayList<Team>();
                if (teamCursor.getCount() > 0) {
                    teamCursor.moveToFirst();
                    do {
                        Team team = new Team();
                        team.setTeamID(teamCursor.getInt(teamCursor.getColumnIndex(Constants.teamID)));
                        team.setTeamName(teamCursor.getString(teamCursor.getColumnIndex(Constants.teamname)));
                        teams.add(team);

                    } while (teamCursor.moveToNext());

                }
                user.setUserTeams(teams);

            }
        }


        return user;
    }

    private ArrayList<App> getAppsbyUser(int empid, int type) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<App> AppsList = new ArrayList<App>();
        String AppsQuery = "";
        if (type == 0) //primary apps
            AppsQuery = "Select " + Constants.appId + " from " + Constants.appPrimaryUserTable + " where " + Constants.empId + " = ?";
        else if (type == 1)
            AppsQuery = "Select " + Constants.appId + " from " + Constants.appSecondaryUserTable + " where " + Constants.empId + " = ?";
        String selArgs[] = new String[]{String.valueOf(empid)};
        Cursor Appscursor = db.rawQuery(AppsQuery, selArgs);
        Integer Appid[] = new Integer[Appscursor.getCount()];
        Log.e("get apps by user type", type + "");
        int indx = 0;
        if (Appscursor.getCount() > 0) {
            Appscursor.moveToFirst();
            do {
                Appid[indx++] = Appscursor.getInt(Appscursor.getColumnIndex(Constants.appId));
            } while (Appscursor.moveToNext());

            String AppdetailsQuery = "Select " + Constants.appId + " , " + Constants.appName + " , " + Constants.team + " from " + Constants.appTable + " where " +
                    Constants.appId + " IN (" + makePlaceholders(Appid.length) + ") order by " + Constants.appName;
            String selectionargs[] = getStringArray(Appid);
            Log.e("appid length", Appid.length + "");
            Cursor appdetailsCursor = db.rawQuery(AppdetailsQuery, selectionargs);
            if (appdetailsCursor.getCount() > 0) {
                appdetailsCursor.moveToFirst();
                do {
                    App app = new App();
                    app.setAppId(appdetailsCursor.getInt(appdetailsCursor.getColumnIndex(Constants.appId)));
                    app.setAppName(appdetailsCursor.getString(appdetailsCursor.getColumnIndex(Constants.appName)));
                    app.setTeamId(appdetailsCursor.getInt(appdetailsCursor.getColumnIndex(Constants.team)));
                    AppsList.add(app);
                } while (appdetailsCursor.moveToNext());

            }

        }
        return AppsList;
    }

    public ArrayList<Tower> getAllTowers() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tower> towers = new ArrayList<Tower>();
        String all_towersqry = "Select " + Constants.towerId + " , " + Constants.towername + " from " + Constants.towerTable;
        Cursor towerCursor = db.rawQuery(all_towersqry, null);
        Log.e("towers curs length", towerCursor.getCount() + "");
        if (towerCursor.getCount() > 0) {
            towerCursor.moveToFirst();
            do {
                Tower tower = new Tower();
                tower.setTowerId(towerCursor.getInt(towerCursor.getColumnIndex(Constants.towerId)));
                tower.setTowerName(towerCursor.getString(towerCursor.getColumnIndex(Constants.towername)));

                towers.add(tower);

            } while (towerCursor.moveToNext());
        }

        return towers;
    }

    public ArrayList<Team> getTeamsByTower(int towerid) {
        Log.e("towerid for teams", towerid + " ");
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Team> teams = new ArrayList<Team>();
        String teams_by_tower_qry = "Select " + Constants.teamID + " , " + Constants.teamname + " from " + Constants.teamTable +
                " Where " + Constants.towerId + " = " + towerid;
        // String selectionArgs[]=new String[]{String.valueOf(towerid)};
        Cursor teamCursor = db.rawQuery(teams_by_tower_qry, null);
        Log.e("teamcursor count", teamCursor.getCount() + "");
        if (teamCursor.getCount() > 0) {
            Log.e("teamCursor len", teamCursor.getCount() + "");
            teamCursor.moveToFirst();
            do {
                Team team = new Team();
                team.setTeamName(teamCursor.getString(teamCursor.getColumnIndex(Constants.towername)));
                team.setTeamID(teamCursor.getInt(teamCursor.getColumnIndex(Constants.teamID)));
                teams.add(team);


            } while (teamCursor.moveToNext());
        }

        return teams;

    }

    private String[] getStringArray(Integer arr[]) {
        int arrLength = arr.length;
        String selectionargs[] = new String[arrLength];
        for (int i = 0; i < arrLength; i++)
            selectionargs[i] = arr[i].toString();
        return selectionargs;
    }

    public ArrayList<App> getAppsbyTeams(Integer teamIDs[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e("teamId size", teamIDs.length + " ");
        if (teamIDs.length > 0)
            Log.e("teamid", teamIDs[0] + "");
        ArrayList<App> apps = new ArrayList<App>();
        String appsbyTeamQry = "Select " + Constants.appId + " , " + Constants.appName + " from " + Constants.appTable + " Where "
                + Constants.team + " IN (" + makePlaceholders(teamIDs.length) + ")";

        String selectionargs[] = getStringArray(teamIDs);


        Cursor appsCursor = db.rawQuery(appsbyTeamQry, selectionargs);
        Log.e("appsCursor len", appsCursor.getCount() + "");
        if (appsCursor.getCount() > 0) {
            appsCursor.moveToFirst();
            do {
                App app = new App();
                app.setAppName(appsCursor.getString(appsCursor.getColumnIndex(Constants.appName)));
                app.setAppId(appsCursor.getInt(appsCursor.getColumnIndex(Constants.appId)));
                apps.add(app);
            } while (appsCursor.moveToNext());
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

    public String getUsername(int empID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username_qry = "Select " + Constants.username + " from " + Constants.userTable + " where " + Constants.empId + " = ?";
        String sel_args[] = new String[]{String.valueOf(empID)};
        Cursor userNameCursor = db.rawQuery(username_qry, sel_args);
        if (userNameCursor.getCount() > 0) {
            userNameCursor.moveToFirst();
            return userNameCursor.getString(userNameCursor.getColumnIndex(Constants.username));
        }

        return "";
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int updateProfessionalProfile(String currentUser, String str_tools, Integer[] primaryappIDs, Integer[] secondaryappIDs, String str_program_langs, int selectedTowerid) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            int empid = Integer.parseInt(currentUser);
            String selectUser = "Select * from " + Constants.userTable + " where " + Constants.empId + " = " + empid;
            Cursor c = db.rawQuery(selectUser, null);
            ContentValues userContentValues = new ContentValues();
            userContentValues.put(Constants.programming_langs, str_program_langs);
            userContentValues.put(Constants.tools, str_tools);
            userContentValues.put(Constants.tower, selectedTowerid);
            String where = Constants.empId + " = ?";
            String[] whereArgs = {"" + currentUser};
            if (c.getCount() > 0) {


                db.update(Constants.userTable, userContentValues, where, whereArgs);

            } else {
                userContentValues.put(Constants.empId, empid);
                db.insert(Constants.userTable, null, userContentValues);
            }

            db.delete(Constants.appPrimaryUserTable, where, whereArgs);

            for (int i = 0; i < primaryappIDs.length; i++) {
                ContentValues priAppContentValues = new ContentValues();
                priAppContentValues.put(Constants.appId, primaryappIDs[i]);
                priAppContentValues.put(Constants.empId, empid);
                db.insert(Constants.appPrimaryUserTable, null, priAppContentValues);
            }
            db.delete(Constants.appSecondaryUserTable, where, whereArgs);
            for (int i = 0; i < secondaryappIDs.length; i++) {
                ContentValues secAppContentValues = new ContentValues();
                secAppContentValues.put(Constants.appId, secondaryappIDs[i]);
                secAppContentValues.put(Constants.empId, empid);
                db.insert(Constants.appSecondaryUserTable, null, secAppContentValues);
            }
            return 1;
        } catch (SQLException e) {
            Log.e("professional prof", e.toString());
            return -1;
        }
    }
}
