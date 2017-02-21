package shivtech.eiger.models;

/**
 * Created by Sivanatarajan on 25-01-2017.
 */

public class Tower {

    private  int towerId;

    private  String towerName;
    private  String towerManager;
    public Tower() {
    }



    public  int getTowerId() {
        return towerId;
    }

    public  void setTowerId(int towerId) {
        this.towerId = towerId;
    }

    public  String getTowerName() {
        return towerName;
    }

    public  void setTowerName(String towerName) {
        this.towerName = towerName;
    }

    public  String getTowerManager() {
        return towerManager;
    }

    public  void setTowerManager(String towerManager) {
        this.towerManager = towerManager;
    }

    @Override
    public String toString() {
        return this.towerName;
    }

}
