package com.example.llcgs.android_rx.bean;

/**
 * com.example.llcgs.android_rx.bean.User
 *
 * @author liulongchao
 * @since 2017/6/13
 */


public class User {

    private String name;
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public User(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "name:"+name+",pwd:"+pwd;
    }
}
