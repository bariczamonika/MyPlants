package ie.dbs.myplants;

public class PlantAndProfilePic {
    private String plantName;
    private String path;
    private String plantID;

    public PlantAndProfilePic()
    {}

    public PlantAndProfilePic(String plantID,String plantName, String path) {
        this.plantID=plantID;
        this.plantName = plantName;
        this.path = path;
    }

    public String getPlantID() {
        return plantID;
    }

    public void setPlantID(String plantID) {
        this.plantID = plantID;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
