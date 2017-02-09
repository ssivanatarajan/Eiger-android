package shivtech.eiger.models;

import java.util.ArrayList;

/**
 * Created by Sivanatarajan on 04-02-2017.
 */

public class AppJSONModel {


    private int appId;
    private String appName;
    private int appTowerID;

    private String appAlias;
    private int appTeamID;
    private String appCategorry;
    private String appSupportLevel;



    public String getAppSupportLevel() {
        return appSupportLevel;
    }

    public void setAppSupportLevel(String appSupportLevel) {
        this.appSupportLevel = appSupportLevel;
    }

    public int getAppTowerID() {
        return appTowerID;
    }

    public void setAppTowerID(int appTowerID) {
        this.appTowerID = appTowerID;
    }

    public int getAppTeamID() {
        return appTeamID;
    }

    public void setAppTeamID(int appTeamID) {
        this.appTeamID = appTeamID;
    }

    public String getAppCategorry() {
        return appCategorry;
    }

    public void setAppCategorry(String appCategorry) {
        this.appCategorry = appCategorry;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }



    public String getAppAlias() {
        return appAlias;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }



    public AppJSONModel(){}
    public AppJSONModel(int appId, String appName,String appAlias, int appTower) {
        this.appId = appId;
        this.appName = appName;
        this.appAlias=appAlias;
        this.appTowerID=appTower;
    }
    public String getAppName()
    {
        return this.appName;
    }

    public AppJSONModel( int appId, String appName, String appAlias,String appCategory,int tower,int team,String supportLevel) {

        this.appId = appId;
        this.appName = appName;
        this.appTowerID = tower;
        this.appTeamID=team;
        this.appSupportLevel=supportLevel;

        this.appAlias = appAlias;
        this.appCategorry=appCategory;
    }
}
