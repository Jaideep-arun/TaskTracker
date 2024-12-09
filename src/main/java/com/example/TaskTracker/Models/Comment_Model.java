package com.example.TaskTracker.Models;

import lombok.Data;

@Data
public class Comment_Model {
    String comment_id;
    String ticket_id;
    String user_id;
    String message;
}
