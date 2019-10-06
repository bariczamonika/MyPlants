package ie.dbs.myplants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlantImage {
    private String picturePath;

    public PlantImage()
    {}

    public PlantImage(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPicturePath() {
        return picturePath;
    }



    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

}
