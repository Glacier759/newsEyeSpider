
package com.xiyoulinux.newseye;

import com.xiyoulinux.newseye.mysqloperation.MysqlOperation;
import com.xiyoulinux.newseye.mysqloperation.NewsInfo;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-9-22.
 */
public class SaveData {
    public DataInfo dataInfo = new DataInfo();
    Reader reader = null;
    SqlSessionFactory ssf = null;
    SqlSession session = null;
    MysqlOperation mapper = null;
    {
        try {
            reader = Resources.getResourceAsReader("mybatis.xml");
            ssf = new SqlSessionFactoryBuilder().build(reader);
            session = ssf.openSession();
            mapper = session.getMapper(MysqlOperation.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveToDisk() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            NewsInfo newsInfo = new NewsInfo();
            newsInfo.title = TitleUtils.half2Fullchange(dataInfo.title);
            newsInfo.url = dataInfo.source;
            newsInfo.newspaper = dataInfo.newspaper;
            newsInfo.page = dataInfo.page;
            newsInfo.publish_date = new Date(format.parse(dataInfo.publishDate).getTime());
            newsInfo.crawl_date = new Date(format.parse(dataInfo.crawlDate).getTime());
            String body = dataInfo.body + "\n";
            for ( String imgSrc:dataInfo.img ) {
                body += "<img src=\"" + imgSrc + "\">\n";
            }
            newsInfo.body = body;

            int count = mapper.selectIsExist(newsInfo);     //首先判断当前记录是否已经获取过
            if ( count > 0 )                               //返回值大于０表示已经获取过该条记录
                return;                                     //获取过即退出
            Integer news_id = mapper.selectWireCopy(newsInfo.title);    //判断当前记录是否在通稿中出现
            if ( news_id == null )                          //返回值为null表示没有出现
                return;                                      //未在通告出现的记录略过
            newsInfo.news_id = news_id;
            mapper.insertNewsInfo(newsInfo);                //将该条记录插入数据库中
            mapper.updateWireCopy(news_id);                 //更新wire_copy中数据
            session.commit();                               //提交事物
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
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
