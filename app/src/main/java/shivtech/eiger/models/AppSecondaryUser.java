package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 03-02-2017.
 */

public class AppSecondaryUser {

    private int empId;
    private int appId;
    private int id;
    private String lastModified;

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int userId) {
        this.empId = userId;
    }
}
