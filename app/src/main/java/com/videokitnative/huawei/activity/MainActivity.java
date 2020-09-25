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
import com.videokitnative.huawei.utils.Constants;

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
        movieList.add(new Movie(1, Constants.MEDIA1_NAME, Constants.MEDIA1_URL, R.drawable.bigbuckbunny));
        movieList.add(new Movie(2, Constants.MEDIA2_NAME, Constants.MEDIA2_URL, R.drawable.sintel));
        movieList.add(new Movie(3,Constants.MEDIA3_NAME,Constants.MEDIA3_URL,R.drawable.tearsofsteel));
        adaptor = new VideoAdaptor(this, movieList,this);
        recyclerView.setAdapter(adaptor);
    }

    @Override
    public void onMovieListener(int position) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("name",movieList.get(position).getName());
        intent.putExtra("url",movieList.get(position).getUrl());
        context.startActivity(intent);
    }
}