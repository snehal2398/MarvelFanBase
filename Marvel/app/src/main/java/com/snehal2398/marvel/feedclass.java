package com.snehal2398.marvel;

/**
 * Created by Snehal on 24-03-2018.
 */

public class feedclass {
    private String title;
    private String image;
    private String url;
    private String desc;
    private String time;

    public feedclass(String title, String image, String url,String desc,String time) {
        this.title = title;
        this.image = image;
        this.url = url;
        this.desc=desc;
        this.time=time;
    }
public feedclass(){

}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
