
package com.xiyoulinux.newseye;

import com.xiyoulinux.newseye.mysqloperation.MysqlOperation;
import com.xiyoulinux.newseye.mysqloperation.NewsInfo;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by glacier on 14-9-22.
 */
public class NewsEyeSpider {
    public static Logger logger = Logger.getLogger(NewsEyeSpider.class.getName());
    {
        //PropertyConfigurator.configure("log4jconfig.xml");
        DOMConfigurator.configure("log4j.xml");
    }

    public static void main(String[] args) {
        /*try {
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
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }*/

    }
}
