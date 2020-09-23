package com.videokitnative.huawei;

public class Movie {
    private int id;
    private String name,url;
    private int image;

    public Movie(int id, String name, String url, int image) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getImage() {
        return image;
    }
}
