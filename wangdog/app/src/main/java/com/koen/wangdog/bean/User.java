package com.koen.wangdog.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by koen on 2016/1/16.
 */
public class User extends BmobChatUser{

    private BmobRelation blogs;
    private String sortLetters;

    private Boolean sex;
    private Integer hight;

    public Integer getHight() {
        return hight;
    }
    public void setHight(Integer hight) {
        this.hight = hight;
    }
    public BmobRelation getBlogs() {
        return blogs;
    }
    public void setBlogs(BmobRelation blogs) {
        this.blogs = blogs;
    }
    public Boolean getSex() {
        return sex;
    }
    public void setSex(Boolean sex) {
        this.sex = sex;
    }
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
