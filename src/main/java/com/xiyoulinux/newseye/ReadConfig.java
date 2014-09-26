
package com.xiyoulinux.newseye;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

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
        public String newspaper, startUrl, encode;
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
            xmlDoc = saxReader.read(new File("SpiderConfig.conf"));
            root = xmlDoc.getRootElement();
            List<?> classList = root.elements("class");

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

    public void saveXML() {
        System.out.println(formatXML());
    }

    public String formatXML() {
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



    /*public void init() {
        xmlDoc = DocumentHelper.createDocument();
        root = xmlDoc.addElement("SpiderConfig");

        Config configObj = new Config();
        configObj.newspaper = "林达意日报";
        configObj.startUrl = "www.lindayi.tk";
        configObj.ruleBody = "body";
        configObj.ruleCrawlDate = "222222";
        configObj.ruleImg = "iiiiiii";
        configObj.rulePage = "pppp";
        configObj.rulePublishDate = "pdpdpdpd";
        configObj.ruleSource = "sssssss";
        configObj.ruleTitle = "ttttttt";
        //addConfigXML(configObj);
    }

     public void addConfigXML( Config configObj ) {
        Element newspaper = root.addElement("newspaper");
        newspaper.addAttribute("name", configObj.newspaper);
        newspaper.addAttribute("starturl", configObj.startUrl);
        Element ruleTitle = newspaper.addElement("ruleTitle");
        ruleTitle.addText(configObj.ruleTitle);
        Element ruleSource = newspaper.addElement("ruleSource");
        ruleSource.addText(configObj.ruleSource);
        Element rulePage = newspaper.addElement("rulePage");
        rulePage.addText(configObj.rulePage);
        Element rulePublishDate = newspaper.addElement("rulePublishDate");
        rulePublishDate.addText(configObj.rulePublishDate);
        Element ruleCrawlDate = newspaper.addElement("ruleCrawlDate");
        ruleCrawlDate.addText(configObj.ruleCrawlDate);
        Element ruleBody = newspaper.addElement("ruleBody");
        ruleBody.addText(configObj.ruleBody);
        Element ruleImg = newspaper.addElement("ruleImg");
        ruleImg.addText(configObj.ruleImg);
    }*/
}
