package com.me.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Video {
    private static Logger logger= LoggerFactory.getLogger(Video.class);

    private int threadNum=Global.threadNum;

    public static volatile int totalNum=0;

    public static volatile int failNum=0;

    public static AtomicInteger finishedNum=new AtomicInteger(0);

    public static AtomicLong downloadedByte=new AtomicLong(0);

    public static volatile Date startDate;

    public void run(){
        totalNum=Global.tsUrls.length;
        int total=totalNum/Global.mergeNum+1;
        logger.info("url total num is {},pageSize {},pageNum {}"
                ,totalNum,Global.mergeNum,total);
        if(Global.endNum!=-1){
            int endNum=Math.min(Global.endNum,totalNum/Global.mergeNum+1);
            total=endNum-Global.startNum;
        }
        int oneNum=(total-1)/threadNum+1;
        Thread[] threads=new Thread[threadNum];
        startDate=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        logger.info("download start date is {}", dateFormat.format(startDate));
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
        Date endDate=new Date();
        long costMinite = (endDate.getTime() - startDate.getTime()) / 1000 / 60;
        logger.info("download finished date is {},cost time {} minute",dateFormat.format(endDate),costMinite);
        try {
            mergeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("文件合并成功");
        logger.info("文件下载成功");

    }

    private void mergeFile() throws Exception {
        if(threadNum<2){
            logger.info("不需要合并文件");
            return;
        }
        byte[] buffer=new byte[1<<16];
        String fileA=String.format(Global.outFile,0);
        File a=new File(fileA);
        FileOutputStream outputStream=null;
        try {
            outputStream=new FileOutputStream(a,true);
            for(int i=1;i<threadNum;i++){
                File b=new File(String.format(Global.outFile,i));
                FileInputStream inputStream=null;
                try {
                    inputStream=new FileInputStream(b);
                    FileIo.writeBytes(inputStream, outputStream,buffer);
                } catch (Exception e) {
                    logger.error("合并文件时发生异常",e);
                }finally {
                    inputStream.close();
                    b.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            outputStream.close();
        }

        logger.info("合并文件完成");
    }
}
