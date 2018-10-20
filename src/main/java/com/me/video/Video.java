package com.me.video;

public class Video {

    public void run(){
        int threadNum=Global.threadNum;
        int total=Global.tsUrls.length;
        int oneNum=total/threadNum+1;
        Thread[] threads=new Thread[threadNum];
        for(int i=0;i<threadNum;i++){
            threads[i]=new Thread(new DownThread(i*oneNum,(i+1)*oneNum,i));
        }
        for(int i=0;i<threadNum;i++){
            threads[i].start();
        }
        for(int i=0;i<threadNum;i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
