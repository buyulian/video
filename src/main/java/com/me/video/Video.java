package com.me.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Video {
    private static Logger logger= LoggerFactory.getLogger(Video.class);

    private int threadNum=Global.threadNum;

    public void run(){
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
        try {
            mergeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("文件下载成功");

    }

    private void mergeFile() throws Exception {
        if(threadNum<2){
            logger.error("不需要合并文件");
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

        logger.error("合并文件完成");
    }
}
