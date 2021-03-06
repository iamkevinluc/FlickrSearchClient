package com.example.flickrsearchclient;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface FlickrApi {
    @GET("/services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    Observable<FlickrSearchResponse> searchPhotos(@Query("api_key") String apiKey,
                                         @Query("tags") String tags,
                                         @Query("per_page") int limit);

}