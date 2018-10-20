package com.me.video;

public class Main {
    public static void main(String[] args){
        Global.init("config/config.yaml");
        Video video=new Video();
        video.run();
    }
}
