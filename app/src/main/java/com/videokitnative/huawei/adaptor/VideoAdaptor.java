package com.videokitnative.huawei.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.videokitnative.huawei.Movie;
import com.videokitnative.huawei.R;

import java.util.List;

public class VideoAdaptor extends RecyclerView.Adapter<VideoAdaptor.VideoViewHolder>{
    private Context context;
    private List<Movie> movieList;
    private onMovieListener MonMovieListener;
    public VideoAdaptor(Context context, List<Movie> movieList , onMovieListener onMovieListener) {
        this.context = context;
        this.movieList = movieList;
        this.MonMovieListener = onMovieListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_layout, null);
        VideoViewHolder holder = new VideoViewHolder(view , MonMovieListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.name.setText(movie.getName());
        holder.url.setText(movie.getUrl());
        holder.imageview.setImageDrawable(context.getResources().getDrawable(movie.getImage()));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageview;
        TextView name, url;
        onMovieListener onMovieListener;
        public VideoViewHolder(@NonNull View itemView , onMovieListener onMovieListener) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.textViewName);
            url = itemView.findViewById(R.id.textViewUrl);
            this.onMovieListener = onMovieListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMovieListener.onMovieListener(getAdapterPosition());
        }
    }
        public interface onMovieListener {
            void onMovieListener(int position);
        }
}
