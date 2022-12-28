package com.example.test.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    public String userId;
    public String userName;
    public int age;
    public String sex;
    public String address;
    public Date createTime;
    public Date updateTime;

    public User() {
        this.updateTime = new Date();
        this.createTime = new Date();
    }

    public User(String userId, String userName, int age, String sex, String address) {
        this.userId = userId;
        this.userName = userName;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.updateTime = new Date();
        this.createTime = new Date();
    }
}
