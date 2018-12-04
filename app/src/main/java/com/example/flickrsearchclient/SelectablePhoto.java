package com.example.flickrsearchclient;

public class SelectablePhoto extends PhotoItem {

    private boolean isSelected;

    public SelectablePhoto(FlickrSearchResponse.Photo photo, boolean isSelected) {
        super(photo);
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
