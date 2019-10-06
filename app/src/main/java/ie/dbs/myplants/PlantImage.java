package ie.dbs.myplants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlantImage {
    private String picturePath;
    private boolean featured;

    public PlantImage()
    {}

    public PlantImage(String picturePath, boolean featured) {
        this.picturePath = picturePath;
        this.featured = featured;
    }

    public String getPicturePath() {
        return picturePath;
    }



    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
}
