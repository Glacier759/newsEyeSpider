
package com.xiyoulinux.newseye;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.xiyoulinux.newseye.ReadConfig.Config;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by glacier on 14-9-22.
 */
public class GetData {

    private SaveData saveData = new SaveData();
    private HashSet<String> urlSet = new HashSet<String>();
    public void getData( Config config ) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            saveData.dataInfo.encode = "UTF-8";
            saveData.dataInfo.language = "中文";
            saveData.dataInfo.crawlDate = dateFormat.format(new Date());
            for (ReadConfig.newspaperClass newsInfo : config.newspaperList) {
                try {
                    saveData.dataInfo.newspaper = newsInfo.newspaper;
                    String trueLink = getTrueLink(newsInfo.startUrl, newsInfo.encode);
                    if (trueLink.contains("58.42.249.98"))     //可否写到配置文件，对于特别的起始页
                        trueLink = trueLink.substring(0, trueLink.lastIndexOf('/') + 1) + "PageArticleIndexGB.htm";
                    NewsEyeSpider.logger.info("trueLink = " + trueLink);
                    getPageInfo(trueLink, config, newsInfo.encode);
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
        }
    }

    public Document getDocument( String url, String encode ) {
        Document doc = null;
        try {
            if ( encode.equals("UTF-8") ) {
                doc = Jsoup.connect(url)
                        .timeout(50000)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:7.0.1) Gecko/20100101 Firefox/7.0.1")
                        .get();
            }
            else if ( encode.equals("GBK") ) {
                try {
                    URL link = new URL(url);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(link.openStream()));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    doc = Jsoup.parse(buffer.toString(), "utf-8");
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
        }
        return doc;
    }

    public void getNewsInfo( String url, Config config, String encode ) {  //新闻来源url
        Document doc = null;
        try {
            NewsEyeSpider.logger.info("即将获取 " + url);
            doc = getDocument(url, encode);
            Element titleEle = doc.select(config.ruleTitle.titleEle).first();
            Element titleTag = titleEle.select(config.ruleTitle.titleTag).first();
            String title = titleTag.text();         //新闻标题
            String publishDate = "";
            if ( config.rulePublishDate.isURL.equals("true") )
                publishDate = getPublishDate(url, config.rulePublishDate.publishDate, encode);   //新闻发布时间
            else {
                Document publishDoc = getDocument(url, encode);
                Element publishEle = publishDoc.select(config.rulePublishDate.publishEle).first();
                publishDate = publishEle.attr(config.rulePublishDate.publishDateAttr);
            }
            saveData.dataInfo.title = title;
            saveData.dataInfo.publishDate = publishDate;

            //ruleBody规则需要重新定制

            Element bodyEle = doc.select(config.ruleBody.bodyEle).first();
            String content = "";            //新闻正文
            if ( !config.ruleBody.bodyEles.equals("null") ) {
                Elements bodyEles = bodyEle.select(config.ruleBody.bodyEles);
                for ( Element contentEle:bodyEles ) {
                    content += contentEle.text() + "\n";
                }
            }
            else {
                content = bodyEle.text();
                content = content.replaceAll(config.ruleBody.regex, config.ruleBody.replacement);
            }
            saveData.dataInfo.body = content;

            List<String> imgList = new ArrayList<String>();         //新闻正文配图
            Element imgEle = doc.select(config.ruleImg).first();
            if ( imgEle != null ) {
                Elements imgEles = imgEle.select("img[src]");
                for (Element imageEle : imgEles) {
                    String imgSrc = "";
                    if ( encode.equals("UTF-8") )
                        imgSrc = imageEle.attr("abs:src");
                    else if ( encode.equals("GBK") )
                        imgSrc = imageEle.attr("src");
                    imgList.add(imgSrc);
                }
            }
            saveData.dataInfo.img = imgList;
            saveData.saveToDisk();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
    }

    public void getPageInfo( String url, Config config, String encode ) {
        Document doc = null;
        try {
            HashSet<String> pageSet = new HashSet<String>();
            doc = getDocument(url, encode);
            Elements pageEles = null;
            if ( config.rulePage.eleNode.equals("true") ) {
                Element pageTag = doc.select(config.rulePage.pageEle).first();
                pageEles = pageTag.select(config.rulePage.pageEles);
            }
            else if ( config.rulePage.eleNode.equals("false") ) {
                Elements pageTags = doc.select(config.rulePage.pageEle);
                pageEles = pageTags.select(config.rulePage.pageEles);
            }
            for ( Element pageEle:pageEles ) {
                try {
                    String pageLink = null;
                    if (encode.equals("UTF-8"))
                        pageLink = pageEle.attr(config.rulePage.eleAttr);     //版面链接
                    else if (encode.equals("GBK"))
                        pageLink = url.substring(0, url.lastIndexOf('/') + 1) + pageEle.attr(config.rulePage.eleAttr);
                    if (pageSet.contains(pageLink) || (config.rulePage.contains != null && !pageLink.contains(config.rulePage.contains)))
                        continue;
                    if (config.rulePage.contains != null && config.rulePage.contains.equals("#"))
                        pageLink = url;
                    if (config.rulePage.subString != null) {
                        String[] subArry = config.rulePage.subString.split(",");
                        int beginIndex = Integer.parseInt(subArry[0]);
                        int endIndex = Integer.parseInt(subArry[1]);
                        pageLink = pageLink.substring(beginIndex, endIndex);
                    }
                    if (!pageLink.contains("http")) {
                        pageLink = url.substring(0, url.lastIndexOf('/')) + pageLink;
                    }
                    pageSet.add(pageLink);
                    String pageText = pageEle.text();               //版面名称
                    saveData.dataInfo.page = pageText;
                    getNewsLink(pageLink, config, encode);
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
        }
    }

    public void getNewsLink( String url, Config config, String encode ) {
        Document doc = null;
        try {
            doc = getDocument(url, encode);
            Element sourceEle = doc.select(config.ruleSource.sourceEle).first();
            Elements newsEles = sourceEle.select(config.ruleSource.sourceEles);

            for ( Element newsEle:newsEles ) {
                try {
                    String newsLink = "";
                    if (encode.equals("UTF-8"))
                        newsLink = newsEle.attr("abs:href");     //获取新闻网页链接
                    else if (encode.equals("GBK")) {
                        newsLink = newsEle.attr("href");
                        newsLink = url.substring(0, url.lastIndexOf('/') + 1) + newsLink;
                    }

                    if (newsLink.contains("?div"))
                        newsLink = newsLink.substring(0, newsLink.indexOf('?'));
                    if (urlSet.contains(newsLink))
                        continue;
                    urlSet.add(newsLink);
                    saveData.dataInfo.source = newsLink;
                    getNewsInfo(newsLink, config, encode);
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
        }
    }

    public String getPublishDate( String url, String rulePublishDate, String encode ) {
        String publishDateStr = null;
        try {
            SimpleDateFormat dateFormat = null;
            if ( rulePublishDate.equals("xxxx-xx/xx") ) {
                dateFormat = new SimpleDateFormat("yyyy-MM/dd");
                String now = dateFormat.format(new Date());
                now = now.substring(0, 8);
                publishDateStr = url.substring(url.indexOf(now), url.indexOf(now)+10);
            }
            else if ( rulePublishDate.equals("xxxxxxxx") ) {
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                String now = dateFormat.format(new Date());
                now = now.substring(0, 6);
                publishDateStr = url.substring(url.indexOf(now), url.indexOf(now)+8);
            }
            else if ( rulePublishDate.equals("xxxx年xx月xx日") ) {
                dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                Document doc = getDocument(url, encode);
                publishDateStr = doc.select("span[class=blue2]").last().text();
                publishDateStr = publishDateStr.substring(0, publishDateStr.indexOf("星期")-1);
            }
            if ( publishDateStr == null )
                return null;
            Date publishDate = dateFormat.parse(publishDateStr);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            publishDateStr = dateFormat.format(publishDate);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
        return publishDateStr;
    }

    public String getTrueLink( String url, String encode ) {
        Document doc = null;
        try {
            //System.out.println(url);
            doc = getDocument(url, encode);
            if ( doc.toString().contains("location.replace") ) {
                String html = doc.toString();
                return getTrueLink(html.substring(html.indexOf("(\"") + 2, html.lastIndexOf("\"")), encode);
            }
            if ( url.contains("paper.chinaso.com") ) {
                Element jumpEle = doc.select("div[class=newpaper_con]").first();
                url = jumpEle.select("a[href]").attr("abs:href");
                return getTrueLink(url, encode);
            }
            Elements metaEles = doc.select("meta[http-equiv=REFRESH]");
            if ( metaEles.size() == 0 ) {
                //System.out.println(url);
                return url;
            }
            else {
                String content = metaEles.attr("content");
                int firstIndex = content.toUpperCase().indexOf("URL") + 4;
                String link = content.substring(firstIndex);
                if ( link.indexOf('\\') >= 0 )
                    link = link.replace('\\', '/');
                if ( link.contains("http://") )
                    return link;
                String sub = url.substring(url.lastIndexOf('/'));
                if ( sub.length() != 1 )
                    return getTrueLink(url.substring(0, url.lastIndexOf('/')+1) + link, encode);
                else
                    return getTrueLink(url + link, encode);
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            NewsEyeSpider.logger.debug(baos.toString());
        }
        return null;
    }
}
