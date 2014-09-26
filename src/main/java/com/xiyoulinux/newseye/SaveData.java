
package com.xiyoulinux.newseye;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-9-22.
 */
public class SaveData {
    public DataInfo dataInfo = new DataInfo();

    public void saveToDisk() {
        try {
            String fileText = "";
            fileText += dataInfo.title + "\n";
            fileText += dataInfo.source + "\n";
            fileText += dataInfo.newspaper + "\n";
            fileText += dataInfo.page + "\n";
            fileText += dataInfo.publishDate + "\n";
            fileText += dataInfo.crawlDate + "\n";
            fileText += dataInfo.language + "\n";
            fileText += dataInfo.encode + "\n";
            fileText += dataInfo.body + "\n";
            for ( String imgSrc:dataInfo.img ) {
                fileText += "<img src=\"" + imgSrc + "\" />";
            }
            //img暂时缺省
            File savePath = new File(new File(new File(new File("Data"), dataInfo.crawlDate), dataInfo.newspaper), System.currentTimeMillis() + ".txt");
            System.out.println(fileText);
            //FileUtils.writeStringToFile(savePath, fileText);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DataInfo{
    public String title, source;
    public String newspaper, page;
    public String publishDate, crawlDate;
    public String language, encode;
    public String body;
    public List<String> img = new ArrayList<String>();
}
