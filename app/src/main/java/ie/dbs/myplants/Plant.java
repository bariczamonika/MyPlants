package ie.dbs.myplants;

import java.util.Date;

enum Light_Condition {
    Very_sunny(0),Sunny(1), Shady(2), South_facing(3), East_facing(4);
    public final int value;
    Light_Condition(int value) { this.value = value; }

}

public class Plant {
    private String name;
    private String description;
    private Date dateAdded;
    private String notes;
    private Double wateringNeeds;
    private Double fertilizingNeeds;
    private Date lastWatered;
    private Date lastFertilized;
    private Date lastReplanted;
    private boolean outdoorPlant;
    private Light_Condition lightCondition;

    public Plant() {

    }

    public Plant(String name, String description, Date dateAdded, String notes, Double wateringNeeds, Double fertilizingNeeds,  boolean outdoorPlant, Light_Condition lightCondition) {
        this.name = name;
        this.description = description;
        this.dateAdded = dateAdded;
        this.notes = notes;
        this.wateringNeeds = wateringNeeds;
        this.fertilizingNeeds = fertilizingNeeds;
        this.outdoorPlant = outdoorPlant;
        this.lightCondition = lightCondition;
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

    public Double getWateringNeeds() {
        return wateringNeeds;
    }

    public void setWateringNeeds(Double wateringNeeds) {
        this.wateringNeeds = wateringNeeds;
    }

    public Double getFertilizingNeeds() {
        return fertilizingNeeds;
    }

    public void setFertilizingNeeds(Double fertilizingNeeds) {
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

