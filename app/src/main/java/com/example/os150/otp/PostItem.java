package com.example.os150.otp;

import android.net.Uri;

/**
 * Created by os150 on 2020-08-03.
 */

public class PostItem {
    private String PostUid;
    private String Title;
    private String Price;
    private String Contents;
    private String Category;
    private String UserUid;
    private String Nickname;
    private Uri PostImage;

    public Uri getPostImage() {
        return PostImage;
    }

    public void setPostImage(Uri PostImage) {
        this.PostImage = PostImage;
    }

    public String getPostUid() {
        return PostUid;
    }

    public void setPostUid(String PostUid) {
        this.PostUid = PostUid;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String Contents) {
        this.Contents = Contents;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String UserUid) {
        this.UserUid = UserUid;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }
}
