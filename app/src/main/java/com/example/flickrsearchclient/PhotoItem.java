package com.example.flickrsearchclient;

public class PhotoItem {
    private String id;
    private String owner;
    private String title;

    private PhotoItem(String id, String owner, String title) {
        this.id = id;
        this.owner = owner;
        this.title = title;
    }

    public PhotoItem(FlickrSearchResponse.Photo photo) {
        this(photo.getId(), photo.getOwner(), photo.getTitle());
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        PhotoItem itemCompare = (PhotoItem) obj;
        if(itemCompare.getId().equals(this.getId())&&
                itemCompare.getOwner().equals(this.getOwner())&&
                itemCompare.getTitle().equals(this.getTitle()))
            return true;

        return false;
    }
}
