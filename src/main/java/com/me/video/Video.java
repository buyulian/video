package com.me.video;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Video {

    private Map<String,String> defaultHeader =new HashMap<>();

    {
        defaultHeader.put("Content-type", "application/x-www-form-urlencoded");
        defaultHeader.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        defaultHeader.put("Accept-Encoding","gzip,deflate,sdch");
        defaultHeader.put("Accept-Language","zh-CN,zh;q=0.8");
        defaultHeader.put("Connection","keep-alive");
        defaultHeader.put("Host", "ssa.jd.com");
        defaultHeader.put(":authority","www3.yuboyun.com");
        defaultHeader.put(":method","GET");
        defaultHeader.put(":path","/hls/2018/10/19/bkIHp7cI/out094.ts");
        defaultHeader.put(":scheme","https");
        defaultHeader.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        defaultHeader.put("origin","https://yuboyun.com");
    }

    public void run() throws Exception {
        String fileNamePattern="E:\\media\\ts\\%03d.ts";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        String host="https://www3.yuboyun.com";
        String urlPattern="/hls/2018/10/19/bkIHp7cI/out%03d.ts";

        int startNum=23;
        int endNum=24;
        int mergeNum=10;

        int timeOut=1000*100;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut)
                .setSocketTimeout(timeOut).build();

        for(int i=startNum;i<endNum;i++){
            String fileName=String.format(fileNamePattern,1);
            FileOutputStream fileOutputStream=new FileOutputStream(fileName,true);
            int size = 1<<17;
            int bytesRead = 0;
            byte[] buffer = new byte[size];
            for(int j=0;j<mergeNum;j++){
                int cs=i*mergeNum+j;

                String url=String.format(urlPattern,cs);
                HttpGet httpGet=new HttpGet(host+url);

                httpGet.setConfig(requestConfig);

                defaultHeader.forEach(httpGet::setHeader);
                httpGet.setHeader("origin",url);
                CloseableHttpResponse response = null;
                try {
                    response = httpclient.execute(httpGet);
                } catch (Exception e) {
                    System.out.println("链接出现异常: "+cs);
                    e.printStackTrace();
                    continue;
                }
                HttpEntity entity = response.getEntity();

                String contentType = entity.getContentType().getValue();
                if(!contentType.contains("video")){
                    System.out.println("文件已结束，结束 "+cs);
                    EntityUtils.consume(entity);
                    //释放链接
                    response.close();
                    fileOutputStream.close();
                    return;
                }

                InputStream inputStream = entity.getContent();
                int sizeAll=0;
                while ((bytesRead = inputStream.read(buffer, 0, size)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    sizeAll+=bytesRead;
                }
                inputStream.close();
                EntityUtils.consume(entity);
                //释放链接
                response.close();
                System.out.println(String.format("url: %d size:%d",cs,sizeAll));
            }
            fileOutputStream.close();
            System.out.println("已写入文件 "+i);
        }
    }
}
