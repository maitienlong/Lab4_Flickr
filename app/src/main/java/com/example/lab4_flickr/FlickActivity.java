package com.example.lab4_flickr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab4_flickr.model.Photo;
import com.example.lab4_flickr.model.Photos;
import com.example.lab4_flickr.model.Post;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FlickActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String method = "flickr.favorites.getList",
            api_key = "6f3c4406142208714992a9337af3fe3e",
            user_id = "185894931@N02",
            extras = "views, media, path_alias, url_sq, url_t, url_s, url_q, url_m, url_n, url_z, url_c, url_l, url_o\n",
            format = "json";
    private int per_page = 10,
            page = 1,
            nojsoncallback = 1;


    private RecyclerView rcLove;

    private SwipeRefreshLayout swipeRL;
    private Retrofit retrofit;
    private List<Photo> photoList;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flick);

        retrofit =
                MyRetrofitBuilder.getInstance("https://www.flickr.com");

        retrofitService = retrofit.create(RetrofitService.class);

        rcLove = findViewById(R.id.rcLove);



        swipeRL = findViewById(R.id.swipeRL);

        swipeRL.setOnRefreshListener(FlickActivity.this);
        swipeRL.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        LoadPhoto();

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SetUpAdapter();
                swipeRL.setRefreshing(false);
            }
        }, 1000);
    }

    private void SetUpAdapter() {
        LoadPhoto();
    }


    private void LoadPhoto() {
        retrofitService.getAllPost(method, api_key, user_id, extras, format, per_page, page, nojsoncallback).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Post post = response.body();
                Photos photos = post.getPhotos();
                photoList = photos.getPhoto();
//
//                for (int i = 0; i < photoList.size(); i++) {
//                    Log.i("Hisss " + i + " |", photoList.get(i).getId());
//                }

                rcLove.setHasFixedSize(true);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                rcLove.setLayoutManager(staggeredGridLayoutManager);
                AdapterRecycleView adapterRecycleView = new AdapterRecycleView(FlickActivity.this, photoList);
                rcLove.setAdapter(adapterRecycleView);

                AdapterRecycleView.ItemClickSupport.addTo(rcLove).setOnItemClickListener(new AdapterRecycleView.ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                  //      Toast.makeText(FlickActivity.this, "OPPPP", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(FlickActivity.this);

                        View alert = LayoutInflater.from(FlickActivity.this).inflate(R.layout.dialog_photo, null);

                        builder.setView(alert);

                        ImageView imageView = alert.findViewById(R.id.imgPhotoLove);
                        TextView textView = alert.findViewById(R.id.tvName);
                        TextView tvView = alert.findViewById(R.id.tvView);
                        textView.setText(photoList.get(position).getTitle());

                        final Thread myThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = loadImageFromNet(photoList.get(position).getUrlL());
                                imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        });
                        myThread.start();

                        builder.create().show();
                    }
                });

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

    }

    private Bitmap loadImageFromNet(String link) {

        URL url;
        Bitmap bitmap = null;
        try {
            url = new URL(link);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (IOException e) {
            Log.e("LoadImage", e + "");
        }

        return bitmap;

    }


}