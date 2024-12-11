package com.example.TaskTracker.Routes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/User")
public class UserRoutes {
    @Autowired
    DataSource dataSource;

    private Connection connection = null;
    private PreparedStatement statement = null;

    @GetMapping("/HelloWorld")
    public String HelloWorld(){
        return "Hello World";
    }

    @PostMapping("/createTicket")
    public String createTicket(@RequestParam String user_id, @RequestBody JSONObject ticket) throws SQLException {

        connection = dataSource.getConnection();
        ResultSet fetchStatus = connection.prepareStatement("select count(ticket_id) from tickets").executeQuery();
        fetchStatus.next();
        int val = fetchStatus.getInt("count(ticket_id)")+1;
        LocalDate date = LocalDate.now();
        int year = date.getYear() ;

        String ticket_id = "T"+val+"_"+year;
        String query = "INSERT INTO TICKETS VALUES('"+ticket_id+"','"
                        +ticket.get("category_id")+"','"
                        +ticket.get("sub_category_id")+"','"
                        +ticket.get("assignee_Id")+"','"
                        +user_id+"','"
                        +ticket.get("subject")+"','"
                        +ticket.get("description")+"',"
                        +"'Open','"
                        +ticket.get("status_id")+"',"
                        +"CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";

        statement = connection.prepareStatement(query);
        statement.execute();
        connection.close();
        return "Ticket Created Successfully with ticket Number"+ticket_id;

    }

    @GetMapping("/FetchTickets")
    public List<JSONObject> FetchTickets(@RequestParam String  user_id) throws SQLException {
        connection = dataSource.getConnection();
        statement = connection.prepareStatement("select * from tickets where reported_Id='"+user_id+"'");
        ResultSet data = statement.executeQuery();
        List<JSONObject> jsonObjectList = new ArrayList<>();

        if (data.isBeforeFirst()){
            while (data.next()) {
                JSONObject object = new JSONObject();
                object.put("Ticket_id", data.getString("ticket_id"));
                object.put("category_id", data.getString("category_id"));
                object.put("sub_category_id", data.getString("sub_category_id"));
                object.put("subject", data.getString("subject"));
                object.put("priority_id", data.getString("priority_id"));
                object.put("status_id", data.getString("status_id"));
                object.put("assignee_Id", data.getString("assignee_Id"));
                object.put("Link","http://localhost:8080/User/"+data.getString("ticket_id"));


                jsonObjectList.add(object);

                connection.close();
            }
            return jsonObjectList;
        }
         else
            return Collections.emptyList();
    }

    @PostMapping("/ticket/addComment")
    public String addComment(@RequestParam String  ticket_id, String user_id ,@RequestBody String comment ) throws SQLException {
        connection = dataSource.getConnection();
        int exists = connection.prepareStatement("select count(user_id) from User where user_id='"+user_id+"'").getFetchSize();
        if (exists == 1) {
            ResultSet fetchStatus =
                connection.prepareStatement("select count(message) from comments where ticket_id='" + ticket_id + "'")
                    .executeQuery();
            fetchStatus.next();
            int val = fetchStatus.getInt("count(message)") + 1;
            String query =
                "INSERT INTO comments VALUES('" + ticket_id + "_comm_" + val + "','" + ticket_id + "','" + user_id + "','" + comment + "')";
            statement = connection.prepareStatement(query);
            statement.execute();
            connection.close();
            return "Comment Added Successfully to ticket " + ticket_id;
        } else {
            connection.close();
            return "User with user id "+user_id+" does not exist" ;
        }
    }

    @GetMapping("/FetchTickets/ticket")
    public List<JSONObject> FetchTicketsDetail(@RequestParam String ticket_id) throws SQLException {

        PreparedStatement CommentStatement = null;

            connection = dataSource.getConnection();
            String query = "select * from tickets where ticket_id ='" + ticket_id +"'";
            String commentsQuery = "select * from Comments where ticket_id ='"+ ticket_id + "'";
            statement = connection.prepareStatement(query);
            ResultSet data = statement.executeQuery();
            data.next();
            int val = data.getFetchSize();
            if(data.getFetchSize()==1) {
                CommentStatement = connection.prepareStatement(commentsQuery);
                ResultSet comments = CommentStatement.executeQuery();
                List<JSONObject> jsonObjectList = new ArrayList<>();

                if (data.isBeforeFirst()) {
                    data.next();
                    JSONObject object = new JSONObject();
                    object.put("Ticket_id", data.getString("ticket_id"));
                    object.put("category_id", data.getString("category_id"));
                    object.put("sub_category_id", data.getString("sub_category_id"));
                    object.put("subject", data.getString("subject"));
                    object.put("description", data.getString("description"));
                    object.put("priority_id", data.getString("priority_id"));
                    object.put("status_id", data.getString("status_id"));
                    object.put("assignee_Id", data.getString("assignee_Id"));
                    object.put("reported_Id", data.getString("reported_Id"));
                    object.put("create_datetime", data.getString("create_datetime"));
                    object.put("last_modified_datetime", data.getString("last_modified_datetime"));

                    if (comments.isBeforeFirst()) {
                        List<String> commentsinTicket = new ArrayList<>();
                        while (comments.next()) {
                            String commentInTicket =
                                comments.getString("user_id") + ":" + comments.getString("message");
                            commentsinTicket.add(commentInTicket);
                        }
                        object.put("Comments", commentsinTicket);
                    }
                    jsonObjectList.add(object);
                    connection.close();
                    return jsonObjectList;
                } else {
                    JSONObject jsonObject = new JSONObject();
                    List<JSONObject> list = new ArrayList<>();
                    jsonObject.put("Error", "Not Exists blabla");
                    list.add(jsonObject);
                    connection.close();
                    return list;

                }
            }else {
                JSONObject jsonObject = new JSONObject();
                List<JSONObject> list = new ArrayList<>();
                jsonObject.put("Error", "Not Exists");
                list.add(jsonObject);
                connection.close();
                return list;
            }

    }
}
