package com.example.TaskTracker.Models;

import lombok.Data;

@Data
public class Ticket_Model {
    String ticket_id;
    String category_id;
    String sub_category_id;
    String assignee_Id;
    String reported_Id;
    String subject ;
    String description;
    String status_id;
    String priority_id;
    String create_datetime;
    String last_modified_datetime ;
}
