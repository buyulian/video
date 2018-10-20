package com.me.video;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ljc on 2017/8/18.
 */
public class FileIo {
    public static String  readFile(File file){
        BufferedReader bufferedReader=null;
        StringBuilder sb=new StringBuilder();
        try {
            FileReader fileReader=new FileReader(file);
            bufferedReader=new BufferedReader(fileReader);
            String str;
            while ((str=bufferedReader.readLine())!=null){
                sb.append(str).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
    }
    public static Properties readProperties(File file){
        Properties prop = new Properties();
        try{
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream (new FileInputStream(file));
            prop.load(in);     ///加载属性列表
            in.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return prop;
    }

    public static Map<String,Object> readYaml(File file) throws Exception {
        return new Yaml().load(new FileInputStream(file));
    }

    @SuppressWarnings("unchecked")
    public static Object getObjectFromYamlMap(Map<String,Object> prop,String key){
        String[] split = key.split(".");
        Object result=prop;
        Map<String,Object> curProp=null;
        for (String str:split){
            curProp= (Map<String, Object>) result;
            result=prop.get(str);
        }
        return result;
    }

    public static String getStringFromYamlMap(Map<String,Object> prop,String key){
        return getObjectFromYamlMap(prop,key).toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String,Object> getMapFromYamlMap(Map<String,Object> prop,String key){
        return (Map<String, Object>) getObjectFromYamlMap(prop,key);
    }

    @SuppressWarnings("unchecked")
    public static Map<String,String> getStringMapFromYamlMap(Map<String,Object> prop,String key){
        return (Map<String, String>) getObjectFromYamlMap(prop,key);
    }

    public static void writeFile(File file,String content){
        createParentDir(file);
        BufferedWriter out=null;
        try {
            out=new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createParentDir(File file){
        File parentPath=file.getParentFile();
        if(parentPath!=null&&!parentPath.exists()){
            parentPath.mkdirs();
        }
    }

    public static boolean deleteFile(File file){
        if(file.isDirectory()){
            File[] files=file.listFiles();
            for(File f:files){
                deleteFile(f);
            }
        }
        return file.delete();
    }
}
