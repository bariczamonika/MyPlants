package ie.dbs.myplants;

import androidx.annotation.NonNull;

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


class Plant {
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
    private boolean taskWateringChecked;
    private boolean taskFertilizinChecked;

    public Plant() {

    }

    public Plant(Plant anotherPlant){
        this.nextFertilizing=anotherPlant.nextFertilizing;
        this.profilePicPath=anotherPlant.profilePicPath;
        this.plantID=anotherPlant.plantID;
        this.name=anotherPlant.name;
        this.description=anotherPlant.description;
        this.notes=anotherPlant.notes;
        this.dateAdded=anotherPlant.dateAdded;
        this.wateringNeeds=anotherPlant.wateringNeeds;
        this.fertilizingNeeds=anotherPlant.fertilizingNeeds;
        this.lastFertilized=anotherPlant.lastFertilized;
        this.lastReplanted=anotherPlant.lastReplanted;
        this.lastWatered=anotherPlant.lastWatered;
        this.outdoorPlant=anotherPlant.outdoorPlant;
        this.lightCondition=anotherPlant.lightCondition;
        this.nextWatering=anotherPlant.nextWatering;
        this.notificationWatering=anotherPlant.notificationWatering;
        this.notificationFertilizing=anotherPlant.notificationFertilizing;
        this.taskWateringChecked=anotherPlant.taskWateringChecked;
        this.taskFertilizinChecked=anotherPlant.taskFertilizinChecked;

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
        this.taskFertilizinChecked=false;
        this.taskWateringChecked=false;
        this.notificationFertilizing=false;
        this.notificationWatering=false;

    }


    String getProfilePicPath() {
        return profilePicPath;
    }

    void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getPlantID() {
        return plantID;
    }

    Date getNextWatering() {
        return nextWatering;
    }

    void setNextWatering(Date nextWatering) {
        this.nextWatering = nextWatering;
    }

    Date getNextFertilizing() {
        return nextFertilizing;
    }

    void setNextFertilizing(Date nextFertilizing) {
        this.nextFertilizing = nextFertilizing;
    }

    boolean isNotificationWatering() {
        return notificationWatering;
    }

    void setNotificationWatering(boolean notificationWatering) {
        this.notificationWatering = notificationWatering;
    }

    boolean isNotificationFertilizing() {
        return notificationFertilizing;
    }

    void setNotificationFertilizing(boolean notificationFertilizing) {
        this.notificationFertilizing = notificationFertilizing;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    Date getDateAdded() {
        return dateAdded;
    }


    String getNotes() {
        return notes;
    }

    void setNotes(String notes) {
        this.notes = notes;
    }

    Watering_Needs getWateringNeeds() {
        return wateringNeeds;
    }

    void setWateringNeeds(Watering_Needs wateringNeeds) {
        this.wateringNeeds = wateringNeeds;
    }

    Fertilizing_Needs getFertilizingNeeds() {
        return fertilizingNeeds;
    }

    void setFertilizingNeeds(Fertilizing_Needs fertilizingNeeds) {
        this.fertilizingNeeds = fertilizingNeeds;
    }

    Date getLastWatered() {
        return lastWatered;
    }

    void setLastWatered(Date lastWatered) {
        this.lastWatered = lastWatered;
    }

    Date getLastFertilized() {
        return lastFertilized;
    }

    void setLastFertilized(Date lastFertilized) {
        this.lastFertilized = lastFertilized;
    }

    Date getLastReplanted() {
        return lastReplanted;
    }

    void setLastReplanted(Date lastReplanted) {
        this.lastReplanted = lastReplanted;
    }

    boolean isOutdoorPlant() {
        return outdoorPlant;
    }

    void setOutdoorPlant(boolean outdoorPlant) {
        this.outdoorPlant = outdoorPlant;
    }

    Light_Condition getLightCondition() {
        return lightCondition;
    }

    boolean isTaskWateringChecked() {
        return taskWateringChecked;
    }

    void setTaskWateringChecked(boolean taskWateringChecked) {
        this.taskWateringChecked = taskWateringChecked;
    }

    boolean isTaskFertilizinChecked() {
        return taskFertilizinChecked;
    }

    void setTaskFertilizinChecked(boolean taskFertilizinChecked) {
        this.taskFertilizinChecked = taskFertilizinChecked;
    }

    void setLightCondition(Light_Condition lightCondition) {
        this.lightCondition = lightCondition;


    }
}

