package shivtech.eiger.utils;

import java.util.Map;

/**
 * Created by Sivanatarajan on 24-01-2017.
 */

public class Constants {

    //public static final String currentUser="CurrentUser";
    public static  final String TWITTER_KEY = "IIzgD5IKNSeEnGYMzSTPw7b8c";
    public static  final String TWITTER_SECRET = "fs3D4hXAiBm67vYkbSMJWBqctPAJPIat0D9OziPoUTJQrjyyZ5";

    public static int currentUserEmpid;
    public static String currentUserName="";
    //dbname
    public static final String dbName="Eiger";
    public static final short dbVersion=1;
    public static  Map<Integer,String> towerNames;

    public static final String verifyIntentFilter="VerifyIntent";
    public static final String EXTRA_PHONE_NUMBER="digit_phoneno";

    //tables
    public static final String userTable="users";
    public static final String appTable="apps";
    public static final String towerTable="towers";
    public static final String teamTable="teams";
    public static final  String appPrimaryUserTable="appprimaryusermappings";
    public static final String appSecondaryUserTable="appsecondaryusermappings";

    //column for check the records which needed to upload
    public static final String isupdate="isUpdate";
    public static final String lastModified="lastModified";

    //columns
    public static final String _id="_id";
    public static final String id="id";
    public static final String appId="appID";
    public static final String appName="name";
    public static final String alias="alias";
    public static final String category="category";
    public static final String team="team";
    public static final String tower="tower";
    public static final String supportLevel="supportLevel";


    public static final String userId="userID";
    public static final String userRole="userRole";
    public static final String empId="empID";
    public static final String username="name";
    public static final String mobile="mobile";
    public static final String tcsEmail="tcsEmail";
    public static final String projectEmail="projectEmail";
    public static final String bloodGroup="bloodGroup";
    public static final String dob="dob";
    public static final String summary="summary";
    public static final String tools="tools";
    public static final String programming_langs="programming_langs";
    public static final String hobbies="hobbies";
    public static final String towername="name";
    public static final String manager="manager";
    public static final String towerId="towerID";

    public static final String teamname="name";
    public static final String teamID="teamID";


    //Shared prefs

    public static final String shared_prefs="Eiger_sp";
    public static final String sp_firstLoad="FirstLoad";
    public static final String sp_cur_user_empId="Cur_user_Empid";
    public static final String sp_cur_user_name="Cur_user_name";
    public static final String sp_cur_user_rol="userRole";

    //post params
    public static final String KEY_EMPID="empId";
    public static final String KEY_PASSWORD="password";


}


