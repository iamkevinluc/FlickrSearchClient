package com.example.flickrsearchclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.List;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getCanonicalName();

    RecyclerView mRecyclerView;

    static final String FLICKR_BASE_URL = "https://api.flickr.com";
    static final String TEST_SEARCH = "Weddings";
    private CompositeDisposable disposableList = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "BuildConfig.FlickrApi = "+BuildConfig.FLICKR_API_KEY);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        final Retrofit restAdapter = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(FLICKR_BASE_URL)
                .client(client)
                .build();

        final FlickrApi api = restAdapter.create(FlickrApi.class);
        final String apiKey = BuildConfig.FLICKR_API_KEY;

        //create subscription for search button press
        final Button searchButton = (Button) findViewById(R.id.search_button);
        final TextView searchTextView = (TextView) findViewById(R.id.search_text);

        final Observable<Object> buttonClickObservable = RxView.clicks(searchButton);
        final Observable<String> searchTextInput =
                RxTextView.textChanges(searchTextView).map(CharSequence::toString);

        disposableList.addAll(
                setupEnableButton(searchButton, searchTextInput),
                setupDoSearch(api, apiKey, searchTextView, buttonClickObservable, searchTextInput)
        );

    }

    @NonNull
    private Disposable setupDoSearch(FlickrApi api, String apiKey, TextView searchTextView, Observable<Object> buttonClickObservable, Observable<String> searchTextInput) {
        return buttonClickObservable
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
                    }
                })
                .withLatestFrom(searchTextInput, (e, searchText) -> searchText)
                .doOnNext(searchText -> Log.d(TAG, "Start search with '" + searchText + "'"))
                .flatMap(searchText ->
                        api.searchPhotos(apiKey, searchText, 13)
                                .subscribeOn(Schedulers.io()))
                .map(FlickrSearchResponse::getPhotos)
                .doOnNext(photos -> Log.d(TAG, "Found " + photos.size() + " photos to process"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateList,
                        throwable -> {
                            Log.i(TAG, "Search Photo failed on this error: "+ throwable.getMessage());
                            throwable.printStackTrace();
                        });
    }

    @NonNull
    private Disposable setupEnableButton(Button searchButton, Observable<String> searchTextInput) {
        // check for 3 character input before allowing search button.
        return searchTextInput
                .map(searchText -> searchText.length() >= 3)
                .subscribe(searchButton::setEnabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!disposableList.isDisposed())
            disposableList.dispose();
    }

    private void updateList(List<FlickrSearchResponse.Photo> photos) {
        // Crudely replace the entire adapter
        PhotoAdapter photoAdapter =
                new PhotoAdapter(this, photos);
        mRecyclerView.setAdapter(photoAdapter);
    }
}
