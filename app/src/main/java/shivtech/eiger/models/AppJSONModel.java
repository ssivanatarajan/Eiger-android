package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 04-02-2017.
 */

public class AppJSONModel {

/*
    Model class for parsing JSON content of APPS
 */
    private int appId;
    private String appName;
    private int appTowerID;

    private String appAlias;
    private int appTeamID;
    private String appCategorry;
    private String appSupportLevel;
    private String lastModified;

    public AppJSONModel() {
    }

    public AppJSONModel(int appId, String appName, String appAlias, int appTower) {
        this.appId = appId;
        this.appName = appName;
        this.appAlias = appAlias;
        this.appTowerID = appTower;
    }


    public AppJSONModel(int appId, String appName, String appAlias, String appCategory, int tower, int team, String supportLevel, String lastModified) {

        this.appId = appId;
        this.appName = appName;
        this.appTowerID = tower;
        this.appTeamID = team;
        this.appSupportLevel = supportLevel;
        this.lastModified = lastModified;
        this.appAlias = appAlias;
        this.appCategorry = appCategory;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

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

    public String getAppAlias() {
        return appAlias;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
