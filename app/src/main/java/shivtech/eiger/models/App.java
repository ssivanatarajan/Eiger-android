package shivtech.eiger.models;

import java.util.ArrayList;

/**
 * Created by Sivanatarajan on 24-01-2017.
 */

public class App {

    private int appId;
    private String appName;
    private String appTower;
    private String towerManager;
    private String appAlias;
    private String appTeam;
    private String appCategorry;
    private ArrayList<User> appPrimaryRes;
    private ArrayList<User> appSecondaryRes;
    private String appSupportLevel;

    public String getTowerManager() {
        return towerManager;
    }

    public void setTowerManager(String towerManager) {
        this.towerManager = towerManager;
    }
    public String getAppSupportLevel() {
        return appSupportLevel;
    }

    public void setAppSupportLevel(String appSupportLevel) {
        this.appSupportLevel = appSupportLevel;
    }



    public String getAppTeam() {
        return appTeam;
    }

    public void setAppTeam(String appTeam) {
        this.appTeam = appTeam;
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

    public void setAppTower(String appTower) {
        this.appTower = appTower;
    }

    public String getAppAlias() {
        return appAlias;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }

    public ArrayList<User> getAppPrimaryRes() {
        return appPrimaryRes;
    }

    public void setAppPrimaryRes(ArrayList<User> appPrimaryRes) {
        this.appPrimaryRes = appPrimaryRes;
    }

    public ArrayList<User> getAppSecondaryRes() {
        return appSecondaryRes;
    }

    public void setAppSecondaryRes(ArrayList<User> appSecondaryRes) {
        this.appSecondaryRes = appSecondaryRes;
    }


    public App(){}
    public App(int appId, String appName,String appAlias, String appTower) {
        this.appId = appId;
        this.appName = appName;
        this.appAlias=appAlias;
        this.appTower = appTower;
    }
    public String getAppName()
    {
        return this.appName;
    }
    public String getAppTower()
    {
        return this.appTower;
    }
    public App( int appId, String appName, String appAlias,String appCategory,String tower,String team,String supportLevel) {

        this.appId = appId;
        this.appName = appName;
        this.appTower = tower;
        this.appTeam=team;
        this.appSupportLevel=supportLevel;

        this.appAlias = appAlias;
        this.appCategorry=appCategory;
       }
}
