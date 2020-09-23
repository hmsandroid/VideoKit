package com.videokitnative.huawei.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.videokitnative.huawei.Movie;
import com.videokitnative.huawei.R;
import com.videokitnative.huawei.adaptor.VideoAdaptor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements VideoAdaptor.onMovieListener {
    RecyclerView recyclerView;
    VideoAdaptor adaptor;
    List<Movie> movieList;
    Context context ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieList = new ArrayList<>();
        this.context = this;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        movieList.add(new Movie(1, "Big Buck Bunny", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", R.drawable.bigbuckbunny));
        movieList.add(new Movie(2, "Sintel", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4", R.drawable.sintel));
        movieList.add(new Movie(3,"Tears of Steel","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",R.drawable.tearsofsteel));
        adaptor = new VideoAdaptor(this, movieList,this);
        recyclerView.setAdapter(adaptor);
    }

    public void Play(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);

    }

    @Override
    public void onMovieListener(int position) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("url",movieList.get(position).getUrl());
        context.startActivity(intent);
    }
}