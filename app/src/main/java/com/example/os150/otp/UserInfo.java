package com.example.os150.otp;

/**
 * Created by os150 on 2020-05-19.
 */

public class UserInfo {
    public String name;
    public String nickname;
    public String phonenum;
    public String email;
    public String profileimage;
    public String uid;

    public UserInfo(String name, String nickname, String phonenum, String email, String profileimage){
        this.name = name;
        this.nickname = nickname;
        this.phonenum = phonenum;
        this.email = email;
        this.profileimage = profileimage;
    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public String getNickname(){return nickname;}
    public void setNickname(String nickname){this.nickname=nickname;}
    public String getPhonenum(){return phonenum;}
    public void setPhonenum(String phonenum){this.phonenum=phonenum;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}
    public String getProfileimage(){return profileimage;}
    public void setProfileimage(String profileimage){this.profileimage=profileimage;}
}
