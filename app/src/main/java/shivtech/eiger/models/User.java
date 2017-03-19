package shivtech.eiger.models;

import java.util.ArrayList;

/**
 * Created by Sivanatarajan on 24-01-2017.
 */

public class User {
    //private String _id;

    private String userName;
    private String tcsEmail;
    private int empID;
    private String userMobile;


    private int userTowerID;
    private String towerManager;
    private String dob;
    private String summary;
    private String tools;
    private String programming_langs;
    private String hobbies;
    private String userRole;
    private ArrayList<App> primaryApps;
    private ArrayList<App> secondaryApps;
    private ArrayList<Team> userTeams;
    private String usertowerName;
    private String teams;
    private String lastModified;
    private String projectEmail;
    private String bloodGroup;

    public User(int empid, String userName, String userTower) {
        this.empID = empid;
        this.userName = userName;
        this.usertowerName = userTower;
    }

    public User() {
    }

    public User(String userName, int empid, String userEmail, String userMobile, String userTower, String userManager, String userPrimaryApps, String userSecondaryApps) {

        this.userName = userName;
        this.empID = empid;

        this.userMobile = userMobile;
        this.usertowerName = userTower;
        this.towerManager = userManager;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUsertowerName() {
        return usertowerName;
    }

    public void setUsertowerName(String usertowerName) {
        this.usertowerName = usertowerName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getUserTowerID() {
        return userTowerID;
    }

    public void setUserTowerID(int userTowerID) {
        this.userTowerID = userTowerID;
    }

    public ArrayList<Team> getUserTeams() {
        return userTeams;
    }

    public void setUserTeams(ArrayList<Team> userTeams) {
        this.userTeams = userTeams;
    }

    public ArrayList<App> getPrimaryApps() {
        return primaryApps;
    }

    public void setPrimaryApps(ArrayList<App> primaryApps) {
        this.primaryApps = primaryApps;
    }

    public ArrayList<App> getSecondaryApps() {
        return secondaryApps;
    }

    public void setSecondaryApps(ArrayList<App> secondaryApps) {
        this.secondaryApps = secondaryApps;
    }

    public String getUserTowerName() {
        return usertowerName;
    }

    public void setUserTowerName(String towerName) {
        this.usertowerName = towerName;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getTeams() {
        return teams;
    }

    public void setTeams(String teams) {
        this.teams = teams;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getProgramming_langs() {
        return programming_langs;
    }

    public void setProgramming_langs(String programming_langs) {
        this.programming_langs = programming_langs;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getProjectEmail() {
        return projectEmail;
    }

    public void setProjectEmail(String projectEmail) {
        this.projectEmail = projectEmail;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getTcsEmail() {
        return tcsEmail;
    }

    public void setTcsEmail(String tcsEmail) {
        this.tcsEmail = tcsEmail;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getTowerManager() {
        return towerManager;
    }

    public void setTowerManager(String userManager) {
        this.towerManager = userManager;
    }

}
