package com.example.llcgs.android_rx.rxbus.event;

/**
 * com.example.llcgs.android_rx.rxbus.event.UserEvent
 *
 * @author liulongchao
 * @since 2017/6/9
 */


public class UserEvent {

    private String name;
    private String pwd;
    private String[] array;

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

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public UserEvent(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public UserEvent() {
    }
}
