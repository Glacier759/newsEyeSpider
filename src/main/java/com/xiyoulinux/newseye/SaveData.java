
package com.xiyoulinux.newseye;

import com.sun.beans.decoder.DocumentHandler;
import com.xiyoulinux.newseye.mysqloperation.MysqlOperation;
import com.xiyoulinux.newseye.mysqloperation.NewsInfo;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
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
            Document xmlDoc = DocumentHelper.createDocument();
            Element root = xmlDoc.addElement("root");
            Element title = root.addElement("title");
            title.addText(dataInfo.title);
            Element url = root.addElement("url");
            url.addText(dataInfo.source);
            Element newspaper = root.addElement("newspaper");
            newspaper.addText(dataInfo.newspaper);
            Element page = root.addElement("page");
            page.addText(dataInfo.page);
            Element publishdate = root.addElement("publishdate");
            publishdate.addText(dataInfo.publishDate);
            Element crawldate = root.addElement("crawldate");
            crawldate.addText(dataInfo.crawlDate);
            Element language = root.addElement("language");
            language.addText(dataInfo.language);
            Element encode = root.addElement("encode");
            encode.addText(dataInfo.encode);
            Element body = root.addElement("body");
            body.addText(dataInfo.body);
            for ( String imgSrc:dataInfo.img ) {
                Element img = body.addElement("img");
                img.addAttribute("src", imgSrc);
            }
            File savePath = new File(new File(new File(new File("newsEyeData"),dataInfo.crawlDate),dataInfo.newspaper),System.currentTimeMillis()+".xml");
            FileUtils.writeStringToFile(savePath, formatXML(root), "UTF-8" );
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
    }

    public void saveToDB() {
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
            if ( count > 0 ) {                               //返回值大于０表示已经获取过该条记录
                System.out.println("[数据已存在] " + newsInfo.url);
                NewsEyeSpider.logger.info("[数据已存在] " + newsInfo.url);
                return;                                     //获取过即退出
            }
            Integer news_id = mapper.selectWireCopy(newsInfo.title);    //判断当前记录是否在通稿中出现
            if ( news_id == null )                          //返回值为null表示没有出现
                return;                                      //未在通告出现的记录略过
            newsInfo.news_id = news_id;
            mapper.insertNewsInfo(newsInfo);                //将该条记录插入数据库中
            mapper.updateWireCopy(news_id);                 //更新wire_copy中数据
            System.out.print("[匹配成功] " + newsInfo.url + "\t" + newsInfo.title + "\n");
            NewsEyeSpider.logger.info("[匹配成功] " + newsInfo.url + "\t" + newsInfo.title + "\n");
            session.commit();                               //提交事物
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
    }

    public String formatXML(Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            formatXMLStr = writer.toString();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
        return formatXMLStr;
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
