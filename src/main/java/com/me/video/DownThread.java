package com.me.video;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;

public class DownThread implements Runnable {

    private int startNum;
    private int endNum;
    private int threadId;

    private static Logger logger= LoggerFactory.getLogger(DownThread.class);


    public DownThread(int startNum, int endNum, int threadId) {
        this.startNum = startNum;
        this.endNum = endNum;
        this.threadId = threadId;
    }

    @Override
    public void run(){
        try {
            down();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void down() throws Exception{
        String fileNamePattern=Global.outFile;

        CloseableHttpClient httpclient =Global.httpclient;

        String host=Global.host;

        int mergeNum=Global.mergeNum;
        int timeOut=Global.timeOut;

        String[] tsUrls=Global.tsUrls;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut)
                .setSocketTimeout(timeOut).build();

        int failNum=0;
        int total=0;
        for(int i=startNum;i<endNum;i++){
            String fileName=String.format(fileNamePattern,threadId);
            FileOutputStream fileOutputStream=new FileOutputStream(fileName,true);
            int size = 1<<17;
            int bytesRead = 0;
            byte[] buffer = new byte[size];
            for(int j=0;j<mergeNum;j++){
                int cs=i*mergeNum+j;

                if(cs>=tsUrls.length){
                    fileOutputStream.close();
                    logger.info("已写入文件 "+i);
                    logger.info("所有请求完成,总共 {},失败 {}",total,failNum);
                    return;
                }

                String url=tsUrls[cs];
                HttpGet httpGet=new HttpGet(host+url);

                httpGet.setConfig(requestConfig);

                Global.headers.forEach(httpGet::setHeader);
                httpGet.setHeader("origin",url);
                CloseableHttpResponse response = null;
                try {
                    logger.debug("发起 HTTP 请求,url at {}",cs);
                    total++;
                    response = httpclient.execute(httpGet);
                    logger.debug(" HTTP 请求结束");
                } catch (Exception e) {
                    failNum++;
                    logger.error("链接出现异常: {},failNum is ",cs,failNum,e);
                    continue;
                }
                HttpEntity entity = response.getEntity();

                String contentType = entity.getContentType().getValue();
                if(!contentType.contains("video")){
                    failNum++;
                    logger.error("url 错误，不是 ts 格式，url is {},failNum is {}"+url,failNum);
                    EntityUtils.consume(entity);
                    //释放链接
                    response.close();
                    logger.info("已写入文件 "+i);
                    fileOutputStream.close();
                    return;
                }

                InputStream inputStream = entity.getContent();
                int sizeAll=FileIo.writeBytes(inputStream,fileOutputStream,buffer);
                inputStream.close();
                EntityUtils.consume(entity);
                //释放链接
                response.close();
                logger.info(String.format("url: %d size:%d",cs,sizeAll));
            }
            fileOutputStream.close();
            logger.info("已写入文件 "+i);
        }
    }
}
