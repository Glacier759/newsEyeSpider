package com.xiyoulinux.newseye.mysqloperation;

import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Created by glacier on 14-9-26.
 */
public interface MysqlOperation {

    public void insertNewsInfo(NewsInfo obj);   //讲NewsInfo对象插入news表中

    public NewsInfo selectNewsInfoID(int id);

    public int selectIsExist(NewsInfo obj);     //在news表中找当前信息是不是已经获取过

    public Integer selectWireCopy(String title);    //在wire_copy表中找当前文章是不是通稿的内容 返回通稿id

    public void updateWireCopy(int id);


}
