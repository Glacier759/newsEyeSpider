newsEyeSpider
=============

抓取各报社报纸信息－采用配置文件形式实现的一个简单的可定制爬虫

#项目说明

>为采集各大报社报刊信息所做的网络爬虫

>考虑到项目的可扩展性采用配置文件形式实现可定制

>用户可依据增添配置文件项来扩展爬虫所抓取的信息

>2014-09-26　当前仅实现获取信息

#配置文件说明

>过滤规则采用了Jsoup的过滤形式

>例如

>>a[id=href]过滤出a标签包含有id属性,并且id属性的值为href

>>div[class]过滤出div标签包含有class属性

>>table过滤出table标签

>[更多过滤细节](https://github.com/Glacier759/newsEyeSpider/blob/master/SpiderConfig.readme)
