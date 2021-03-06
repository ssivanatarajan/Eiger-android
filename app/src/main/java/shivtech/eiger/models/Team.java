package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 29-01-2017.
 */

public class Team {
    private int teamID;
    private String teamName;
    private int towerID;
    private String lastModified;

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getTowerID() {
        return towerID;
    }

    public void setTowerID(int towerID) {
        this.towerID = towerID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    @Override
    public String toString() {
        return this.teamName;
    }
}
