package com.me.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Video {
    private static Logger logger= LoggerFactory.getLogger(Video.class);

    public void run(){
        int threadNum=Global.threadNum;
        int total=Global.tsUrls.length/Global.mergeNum+1;
        if(Global.endNum!=-1){
            total=Global.endNum-Global.startNum;
        }
        int oneNum=(total-1)/threadNum+1;
        Thread[] threads=new Thread[threadNum];
        for(int i=0;i<threadNum;i++){
            threads[i]=new Thread(new DownThread(i*oneNum,(i+1)*oneNum,i));
            logger.info("线程 {},start {},end {}",i,i*oneNum,(i+1)*oneNum);
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
