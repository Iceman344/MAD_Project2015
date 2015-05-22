package be.howest.nmct.chilltips.models;

import android.media.Image;

import java.util.List;

/**
 * Created by JukeBox on 11/05/2015.
 */
public class Chilltip {
    private String title;
    private String description;
    private String  previewImage;
    private String location;

    public Chilltip(String title,String description,String location,String previewImage)
    {
        this.title = title;
        this.description =description;
        this.previewImage = previewImage;
        this.location=location;
    }
    public String getPreviewImage() {
        return previewImage;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
