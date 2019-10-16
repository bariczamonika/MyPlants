package ie.dbs.myplants;

import java.util.Date;

public class PlantNotifications {
    private int notificationID;
    private Date notificationDate;
    private String plantID;
    private boolean isWatering;

    public PlantNotifications()
    {}

    public PlantNotifications(int notificationID, Date notificationDate, String plantID, boolean isWatering) {
        this.notificationID = notificationID;
        this.notificationDate = notificationDate;
        this.plantID=plantID;
        this.isWatering=isWatering;
    }

    public String getPlantID() {
        return plantID;
    }

    public void setPlantID(String plantID) {
        this.plantID = plantID;
    }

    public boolean isWatering() {
        return isWatering;
    }

    public void setWatering(boolean watering) {
        isWatering = watering;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }
}
