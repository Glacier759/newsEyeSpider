
package com.xiyoulinux.newseye;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by glacier on 14-9-22.
 */
public class NewsEyeSpider {
    public static Logger logger = Logger.getLogger(NewsEyeSpider.class.getName());
    public static BloomFilter filter = null;
    public static File filterFile = new File("filter.record");    //判断布隆过滤器记录文件是否存在
    {
        DOMConfigurator.configure("log4j.xml");
    }

    public static void main(String[] args) {
        try {
            initBloomFilter();

            ReadConfig readConfig = new ReadConfig();
            List<?> configList = readConfig.readConfig();
            GetData getData = new GetData();
            for (Object configObj : configList) {
                try {
                    ReadConfig.Config config = (ReadConfig.Config) configObj;
                    getData.getData(config);
                }catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    NewsEyeSpider.logger.debug(baos.toString());
                }
            }
            saveBloomFilter();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
    }

    public static void initBloomFilter() {
        try {
            if (filterFile.exists()) {    //如果存在则读入整个对象流
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filterFile));
                filter = (BloomFilter) ois.readObject();
                SimpleDateFormat format = new SimpleDateFormat("dd");
                String today = format.format(new Date());
                if (!today.equals(filter.recordDate))
                    filter = new BloomFilter(100000, 15000);
            } else {
                filter = new BloomFilter(100000, 15000);
            }
            System.out.println("[BloomFilter] 初始化成功");
            logger.info("[BloomFilter] 初始化成功");
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
    }

    public static void saveBloomFilter() {
        try {
            if (filterFile.exists())
                filterFile.delete();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filterFile));
            oos.writeObject(filter);
            System.out.println("[BloomFilter] 保存成功");
            logger.info("[BloomFilter] 保存成功");
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
    }
}
