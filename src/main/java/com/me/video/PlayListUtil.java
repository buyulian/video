package com.me.video;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PlayListUtil {
    public static String[] getUrls(String playList){
        String content=FileIo.readFile(new File(playList));
        String[] lines = content.split("\n");
        List<String> rs=new LinkedList<>();
        String tsPath=Global.tsPath;
        for(int i=0;i<lines.length;i++){
            String line=lines[i];
            if(line.contains("EXTINF")){
                rs.add(tsPath+lines[i+1]);
                i++;
            }else if(line.contains("EXT-X-ENDLIST")){
                break;
            }
        }
        String[] result=new String[rs.size()];
        rs.toArray(result);
        return result;
    }
}
