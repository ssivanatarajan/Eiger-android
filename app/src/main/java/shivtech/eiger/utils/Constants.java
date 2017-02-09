package shivtech.eiger.utils;

import java.util.Map;

/**
 * Created by Sivanatarajan on 24-01-2017.
 */

public class Constants {
    //dbname
    public static String dbName="Eiger";
    public static short dbVersion=1;
    public static Map<Integer,String> towerNames;
    //tables
    public static String userTable="users";
    public static String appTable="apps";
    public static String towerTable="towers";
    public static String teamTable="teams";
    public static  String appPrimaryUserTable="appprimaryusermappings";
    public static String appSecondaryUserTable="appsecondaryusermappings";
    //columns
    public static String _id="_id";
    public static String id="id";
    public static String appId="appID";
    public static String appName="name";
    public static String alias="alias";
    public static String category="category";
    public static String team="team";
    public static String tower="tower";
    public static String supportLevel="supportLevel";


    public static String userId="userID";
    public static String empId="empID";
    public static String username="name";
    public static String mobile="mobile";
    public static String tcsEmail="tcsEmail";
    public static String projectEmail="projectEmail";
    public static String bloodGroup="bloodGroup";
    public static String dob="dob";

    public static String towername="name";
    public static String manager="manager";
    public static String towerId="towerID";

    public static String teamname="name";
    public static String teamID="teamID";


    //Shared prefs

    public static String shared_prefs="Eiger_sp";
    public static String sp_firstLoad="FirstLoad";
    public static String sp_empId="Empid";
}
