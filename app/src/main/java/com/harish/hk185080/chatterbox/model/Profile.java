package com.harish.hk185080.chatterbox.model;

public class Profile {
    private String id;
    private String name;
    private int age;
    private String profile_pic;
    private int distance;

    public Profile(String id, String name, int age, String profile_pic, int distance) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.profile_pic = profile_pic;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfilePic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}


