package io.github.froger.instamaterial.model;

public class User {
    String Name;
    String  Uid;
    String avatar;
    String email;
    String Username;
    String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public User(String name, String uid, String avatar, String email, String username) {
        Name = name;
        Uid = uid;
        this.avatar = avatar;
        this.email = email;
        Username = username;
    }
    public User(String test)
    {
        this.test=test;
    }
    public  User()//important do not delete
    {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
