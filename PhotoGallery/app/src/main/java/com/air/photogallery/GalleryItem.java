package com.air.photogallery;

/**
 * Created by Air on 15/7/29.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    public String toString() {
        return mCaption;
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getCaption() {
        return mCaption;
    }
    public void setCaption(String caption) {
        mCaption = caption;
    }
}
