package com.me.video;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.util.Map;

public class Global {
    public static Map<String,String> headers;

    public static String playList;

    public static String[] tsUrls;

    public static String tsPath;

    public static CloseableHttpClient httpclient= HttpClients.createDefault();;

    public static String host;

    public static String outFile;

    public static int startNum;

    public static int endNum;

    public static int mergeNum;

    public static int timeOut;

    public static int threadNum;

    public static Map<String, Object> prop;

    public static void init(String configFileName){
        try {
            prop = FileIo.readYaml(new File(configFileName));
            headers=FileIo.getStringMapFromYamlMap(prop,"headers");
            Map<String, Object> control = FileIo.getMapFromYamlMap(prop, "control");
            playList=FileIo.getStringFromYamlMap(control,"playList");
            host=FileIo.getStringFromYamlMap(control,"host");
            outFile=FileIo.getStringFromYamlMap(control,"outFile");
            tsPath=FileIo.getStringFromYamlMap(control,"tsPath");
            startNum=Integer.parseInt(FileIo.getStringFromYamlMap(control,"startNum"));
            endNum=Integer.parseInt(FileIo.getStringFromYamlMap(control,"endNum"));
            mergeNum=Integer.parseInt(FileIo.getStringFromYamlMap(control,"mergeNum"));
            threadNum=Integer.parseInt(FileIo.getStringFromYamlMap(control,"threadNum"));
            tsUrls=PlayListUtil.getUrls(playList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取配置文件时发生异常");
        }
    }
}
