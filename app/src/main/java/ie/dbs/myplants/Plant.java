package ie.dbs.myplants;

import java.util.Date;

enum Light_Condition {
    None(0),Very_sunny(1),Sunny(2), Shady(3), South_facing(4), East_facing(5);
    public final int value;
    Light_Condition(int value) { this.value = value; }

}


enum Watering_Needs{
    None(0),Daily(1),Every_2_days(2), Every_3_days(3),
    Every_4_days(4),Every_5_days(5),Every_6_days(6), Weekly(7),
    Every_8_days(8), Every_9_days(9),Every_10_days(10),Every_11_days(11),
    Every_12_days(12), Every_13_days(13), Every_2_weeks(14), Custom_option(15);
    public final int value;
    Watering_Needs(int value) { this.value = value; }

}

enum Fertilizing_Needs {None(0),Weekly(1),Every_2_weeks(2),Every_3_weeks(3),
    Every_month(4),Every_6_weeks(5),Every_2_months(6), Custom_option(7);

    public final int value;
    Fertilizing_Needs(int value) { this.value = value; }
}


public class Plant {
    private String plantID;
    private String name;
    private String description;
    private Date dateAdded;
    private String notes;
    private Watering_Needs wateringNeeds;
    private Fertilizing_Needs fertilizingNeeds;
    private Date lastWatered;
    private Date lastFertilized;
    private Date lastReplanted;
    private boolean outdoorPlant;
    private Light_Condition lightCondition;
    private String profilePicPath;
    private Date nextWatering;
    private Date nextFertilizing;
    private boolean notificationWatering;
    private boolean notificationFertilizing;
    private boolean isWateringNotificationSet;
    private boolean isFertilizingNotificationSet;

    public Plant() {

    }

    public Plant(String plantID, String name, String description, Date dateAdded, String notes, Watering_Needs wateringNeeds,
                 Fertilizing_Needs fertilizingNeeds, boolean outdoorPlant, Light_Condition lightCondition, String profilePicPath) {
        this.plantID=plantID;
        this.name = name;
        this.description = description;
        this.dateAdded = dateAdded;
        this.notes = notes;
        this.wateringNeeds = wateringNeeds;
        this.fertilizingNeeds = fertilizingNeeds;
        this.outdoorPlant = outdoorPlant;
        this.lightCondition = lightCondition;
        this.profilePicPath=profilePicPath;

    }

    public boolean isWateringNotificationSet() {
        return isWateringNotificationSet;
    }

    public void setWateringNotificationSet(boolean wateringNotificationSet) {
        isWateringNotificationSet = wateringNotificationSet;
    }

    public boolean isFertilizingNotificationSet() {
        return isFertilizingNotificationSet;
    }

    public void setFertilizingNotificationSet(boolean fertilizingNotificationSet) {
        isFertilizingNotificationSet = fertilizingNotificationSet;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getPlantID() {
        return plantID;
    }

    public Date getNextWatering() {
        return nextWatering;
    }

    public void setNextWatering(Date nextWatering) {
        this.nextWatering = nextWatering;
    }

    public Date getNextFertilizing() {
        return nextFertilizing;
    }

    public void setNextFertilizing(Date nextFertilizing) {
        this.nextFertilizing = nextFertilizing;
    }

    public boolean isNotificationWatering() {
        return notificationWatering;
    }

    public void setNotificationWatering(boolean notificationWatering) {
        this.notificationWatering = notificationWatering;
    }

    public boolean isNotificationFertilizing() {
        return notificationFertilizing;
    }

    public void setNotificationFertilizing(boolean notificationFertilizing) {
        this.notificationFertilizing = notificationFertilizing;
    }

    public void setPlantID(String plantID) {
        this.plantID = plantID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Watering_Needs getWateringNeeds() {
        return wateringNeeds;
    }

    public void setWateringNeeds(Watering_Needs wateringNeeds) {
        this.wateringNeeds = wateringNeeds;
    }

    public Fertilizing_Needs getFertilizingNeeds() {
        return fertilizingNeeds;
    }

    public void setFertilizingNeeds(Fertilizing_Needs fertilizingNeeds) {
        this.fertilizingNeeds = fertilizingNeeds;
    }

    public Date getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(Date lastWatered) {
        this.lastWatered = lastWatered;
    }

    public Date getLastFertilized() {
        return lastFertilized;
    }

    public void setLastFertilized(Date lastFertilized) {
        this.lastFertilized = lastFertilized;
    }

    public Date getLastReplanted() {
        return lastReplanted;
    }

    public void setLastReplanted(Date lastReplanted) {
        this.lastReplanted = lastReplanted;
    }

    public boolean isOutdoorPlant() {
        return outdoorPlant;
    }

    public void setOutdoorPlant(boolean outdoorPlant) {
        this.outdoorPlant = outdoorPlant;
    }

    public Light_Condition getLightCondition() {
        return lightCondition;
    }

    public void setLightCondition(Light_Condition lightCondition) {
        this.lightCondition = lightCondition;
    }
}

