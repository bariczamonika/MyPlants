package ie.dbs.myplants;

public class PlantAndProfilePic {
    private String plantName;
    private String path;

    public PlantAndProfilePic()
    {}

    public PlantAndProfilePic(String plantName, String path) {
        this.plantName = plantName;
        this.path = path;
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
