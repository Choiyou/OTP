package com.example.os150.otp;

import org.w3c.dom.Comment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by os150 on 2020-06-23.
 */

public class ChatModel {

    public Map<String, Boolean> users = new HashMap<>();
    public Map<String, Comment> comments = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
        public Object timestamp;
    }
}
