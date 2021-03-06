package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 25-01-2017.
 */

public class Tower {

    private int towerId;

    private String towerName;
    private int towerManager;
    private String lastModified;

    public Tower() {
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getTowerId() {
        return towerId;
    }

    public void setTowerId(int towerId) {
        this.towerId = towerId;
    }

    public String getTowerName() {
        return towerName;
    }

    public void setTowerName(String towerName) {
        this.towerName = towerName;
    }

    public int getTowerManager() {
        return towerManager;
    }

    public void setTowerManager(int towerManager) {
        this.towerManager = towerManager;
    }

    @Override
    public String toString() {
        return this.towerName;
    }

}
