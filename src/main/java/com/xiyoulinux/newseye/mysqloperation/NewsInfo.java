package com.xiyoulinux.newseye.mysqloperation;

import java.sql.Date;

/**
 * Created by glacier on 14-9-26.
 */
public class NewsInfo {
    public int id, news_id;
    public String title, url, newspaper, page;
    public Date publish_date, crawl_date;
    public String body;

    public void setId(int id) { this.id = id;   }
    public void setNews_id(int news_id) {   this.news_id = news_id; }
    public void setTitle(String title) {    this.title = title; }
    public void setUrl(String url) {    this.url = url; }
    public void setNewspaper(String newspaper) {    this.newspaper = newspaper; }
    public void setPage(String page) {  this.page = page;   }
    public void setPublish_date(Date publish_date) {    this.publish_date = publish_date;   }
    public void setCrawl_date(Date crawl_date) {    this.crawl_date = crawl_date;   }
    public void setBody(String body) {  this.body = body;   }

    public int getId() {    return id;  }
    public int getNews_id() {   return news_id; }
    public String getTitle() {  return title;   }
    public String getUrl() {    return url; }
    public String getNewspaper() {  return newspaper;   }
    public String getPage() {   return page;    }
    public String getBody() {   return body;    }
    public Date getPublish_date() { return publish_date;    }
    public Date getCrawl_date() {   return crawl_date;  }

    public String toString() {
        return id + "\n" + title + "\n" + url + "\n" + newspaper + "\n" + page + "\n"
                + publish_date + "\n" + crawl_date + "\n" + body + "\n";
    }
}
