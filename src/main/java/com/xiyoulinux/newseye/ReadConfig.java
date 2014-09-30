
package com.xiyoulinux.newseye;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by glacier on 14-9-22.
 */
public class ReadConfig {
    class Config {
        public List<newspaperClass> newspaperList = new ArrayList<newspaperClass>();
        public String ruleImg;
        public publishDateClass rulePublishDate = new publishDateClass();
        public titleClass ruleTitle = new titleClass();
        public sourceClass ruleSource = new sourceClass();
        public pageClass rulePage = new pageClass();
        public bodyClass ruleBody = new bodyClass();
    }
    class newspaperClass {
        public String newspaper, startUrl, encode, getHomePage;
    }
    class titleClass {
        public String titleEle, titleTag;
    }
    class sourceClass {
        public String sourceEle, sourceEles;
    }
    class publishDateClass {
        public String publishDate, isURL, publishEle, publishDateAttr;
    }
    class pageClass {
        public String pageEle, pageEles, eleAttr, subString, eleNode, contains;
    }
    class bodyClass {
        public String bodyEle, bodyEles, regex, replacement;
    }

    private Document xmlDoc;
    private Element root;

    public List<Config> readConfig() {
        List<Config> configList = new ArrayList<Config>();
        try {
            SAXReader saxReader = new SAXReader();
            xmlDoc = saxReader.read(new File("SpiderConfig.debug"));
            root = xmlDoc.getRootElement();
            List<?> classList = root.elements("class");
            NewsEyeSpider.logger.info("[类别总数] " + classList.size());
            System.out.println("[类别总数] " + classList.size());
            int newspaperCount = 0;
            for ( Iterator<?> iter = classList.iterator(); iter.hasNext(); ) {
                Element newspaper = (Element) iter.next();
                newspaperCount += newspaper.elements("newspaper").size();
            }
            NewsEyeSpider.logger.info("[新闻总数] " + newspaperCount);
            System.out.println("[新闻总数] " + newspaperCount);
            for ( Iterator<?> iter = classList.iterator(); iter.hasNext(); ) {
                try {
                    Element newspaper = (Element) iter.next();

                    Config configObj = new Config();
                    List<?> newspaperList = newspaper.elements("newspaper");
                    for (Iterator<?> newsIter = newspaperList.iterator(); newsIter.hasNext(); ) {
                        Element newsInfo = (Element) newsIter.next();
                        newspaperClass newsObj = new newspaperClass();
                        newsObj.newspaper = newsInfo.attributeValue("name");
                        newsObj.startUrl = newsInfo.attributeValue("startUrl");
                        newsObj.encode = newsInfo.attributeValue("encode");
                        newsObj.getHomePage = newsInfo.attributeValue("getHomePage");
                        configObj.newspaperList.add(newsObj);
                    }
                    Element titleEle = newspaper.element("ruleTitle");
                    configObj.ruleTitle.titleEle = titleEle.element("titleEle").getText();
                    configObj.ruleTitle.titleTag = titleEle.element("titleTag").getText();
                    configObj.rulePublishDate.publishDate = newspaper.element("rulePublishDate").getText();
                    if (newspaper.element("rulePublishDate").attributeValue("url") != null) {
                        configObj.rulePublishDate.isURL = newspaper.element("rulePublishDate").attributeValue("url");
                        configObj.rulePublishDate.publishEle = newspaper.element("publishEle").attributeValue("ele");
                        configObj.rulePublishDate.publishDateAttr = newspaper.element("publishEle").getText();
                    } else
                        configObj.rulePublishDate.isURL = "true";
                    configObj.ruleImg = newspaper.element("ruleImg").getText();
                    Element sourceEle = newspaper.element("ruleSource");
                    configObj.ruleSource.sourceEle = sourceEle.element("sourceEle").getText();
                    configObj.ruleSource.sourceEles = sourceEle.element("sourceEles").getText();
                    Element pageEle = newspaper.element("rulePage");
                    configObj.rulePage.pageEle = pageEle.element("pageEle").getText();
                    configObj.rulePage.pageEles = pageEle.element("pageEles").getText();
                    configObj.rulePage.eleAttr = pageEle.element("eleAttr").getText();
                    configObj.rulePage.eleNode = pageEle.element("pageEle").attributeValue("node");
                    if (pageEle.element("eleAttr").attributeValue("contains") != null)
                        configObj.rulePage.contains = pageEle.element("eleAttr").attributeValue("contains");
                    if (pageEle.element("subString") != null)
                        configObj.rulePage.subString = pageEle.element("subString").getText();
                    Element bodyEle = newspaper.element("ruleBody");
                    configObj.ruleBody.bodyEle = bodyEle.element("bodyEle").getText();
                    configObj.ruleBody.bodyEles = bodyEle.element("bodyEles").getText();
                    configObj.ruleBody.regex = bodyEle.element("regex").getText();
                    configObj.ruleBody.replacement = bodyEle.element("replacement").getText();

                    configList.add(configObj);
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
        return configList;
    }
}
