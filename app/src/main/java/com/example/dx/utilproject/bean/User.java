package com.example.dx.utilproject.bean;

public class User {
    public String name;
    public Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name+","+age;
    }
}
