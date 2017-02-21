package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 24-01-2017.
 */

public class User {
    //private String _id;
    private int userId;
    private String userName;
    private String tcsEmail;
    private int empID;
    private String userMobile;
    private int userTower;
    private String userManager;
    private String dob;
    private String summary;
    private String tools;
    private String programming_langs;

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

    private String projectEmail;
    private String bloodGroup;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getUserTower() {
        return userTower;
    }

    public void setUserTower(int userTower) {
        this.userTower = userTower;
    }

    public String getUserManager() {
        return userManager;
    }

    public void setUserManager(String userManager) {
        this.userManager = userManager;
    }


    public User(int userId, String userName, int userTower) {
        this.userId = userId;
        this.userName = userName;
        this.userTower = userTower;
    }
public User(){}
    public User( String userName, int userId, String userEmail, String userMobile, int userTower, String userManager, String userPrimaryApps, String userSecondaryApps) {

        this.userName = userName;
        this.userId = userId;

        this.userMobile = userMobile;
        this.userTower = userTower;
        this.userManager = userManager;
        }

}
