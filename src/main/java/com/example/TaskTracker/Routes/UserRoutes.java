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
    public static final String SUB_CATEGORY_ID = "sub_category_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String SUBJECT = "subject";
    public static final String DESCRIPTION = "description";
    public static final String STATUS_ID = "status_id";
    public static final String  ASSIGNEE_ID = "assignee_Id";
    public static final String TICKET_ID = "ticket_id";
    public static final String PRIORITY_ID = "priority_id";
    @Autowired
    private DataSource dataSource;

    private Connection connection = null;
    private PreparedStatement statement = null;

    @GetMapping("/HelloWorld")
    public String helloWorld(){
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
                        +ticket.get(CATEGORY_ID)+"','"
                        +ticket.get(SUB_CATEGORY_ID)+"','"
                        +ticket.get(ASSIGNEE_ID)+"','"
                        +user_id+"','"
                        +ticket.get(SUBJECT)+"','"
                        +ticket.get(DESCRIPTION)+"',"
                        +"'Open','"
                        +ticket.get(STATUS_ID)+"',"
                        +"CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";

        statement = connection.prepareStatement(query);
        statement.execute();
        connection.close();
        return "Ticket Created Successfully with ticket Number"+ticket_id;

    }

    @GetMapping("/FetchTickets")
    public List<JSONObject> fetchTickets(@RequestParam String  user_id) throws SQLException {
        connection = dataSource.getConnection();
        statement = connection.prepareStatement("select * from tickets where reported_Id='"+user_id+"'");
        ResultSet data = statement.executeQuery();
        List<JSONObject> jsonObjectList = new ArrayList<>();

        if (data.isBeforeFirst()){
            while (data.next()) {
                JSONObject object = new JSONObject();
                object.put("Ticket_id", data.getString(TICKET_ID));
                object.put(CATEGORY_ID, data.getString(CATEGORY_ID));
                object.put(SUB_CATEGORY_ID, data.getString(SUB_CATEGORY_ID));
                object.put(SUBJECT, data.getString(SUBJECT));
                object.put(PRIORITY_ID, data.getString(PRIORITY_ID));
                object.put(STATUS_ID, data.getString(STATUS_ID));
                object.put(ASSIGNEE_ID, data.getString(ASSIGNEE_ID));
                object.put("Link","http://localhost:8080/User/FetchTickets/ticket?ticket_id="+data.getString(TICKET_ID));


                jsonObjectList.add(object);


            }
            connection.close();
            return jsonObjectList;
        }
         else
            return Collections.emptyList();
    }

    @PostMapping("/ticket/addComment")
    public String addComment(@RequestParam String  ticket_id, String user_id ,@RequestBody String comment ) throws SQLException {
        connection = dataSource.getConnection();
        ResultSet user = connection.prepareStatement("select user_id from User where user_id='"+user_id+"'").executeQuery();

        boolean fi = user.isBeforeFirst();


        if (user.isBeforeFirst()) {
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
    public List<JSONObject> fetchTicketsDetail(@RequestParam String ticket_id) throws SQLException {
        PreparedStatement CommentStatement = null;
        connection = dataSource.getConnection();
        String query = "select * from tickets where ticket_id ='" + ticket_id +"'";
        String commentsQuery = "select * from Comments where ticket_id ='"+ ticket_id + "'";
        statement = connection.prepareStatement(query);
        CommentStatement = connection.prepareStatement(commentsQuery);
        ResultSet comments = CommentStatement.executeQuery();
        ResultSet data = statement.executeQuery();

        List<JSONObject> jsonObjectList = new ArrayList<>();
        if (data.isBeforeFirst()){
            data.next();
            JSONObject object = new JSONObject();
            object.put(TICKET_ID, data.getString(TICKET_ID));
            object.put(CATEGORY_ID, data.getString(CATEGORY_ID));
            object.put(SUB_CATEGORY_ID, data.getString(SUB_CATEGORY_ID));
            object.put(SUBJECT, data.getString(SUBJECT));
            object.put("description",data.getString("description"));
            object.put(PRIORITY_ID, data.getString(PRIORITY_ID));
            object.put(STATUS_ID, data.getString(STATUS_ID));
            object.put(ASSIGNEE_ID, data.getString(ASSIGNEE_ID));
            object.put("reported_Id", data.getString("reported_Id"));
            object.put("create_datetime", data.getString("create_datetime"));
            object.put("last_modified_datetime", data.getString("last_modified_datetime"));
            if(comments.isBeforeFirst()){
                List<String> commentsinTicket = new ArrayList<>();
                while (comments.next()){
                    String commentInTicket = comments.getString("user_id")+":"+comments.getString("message");
                    commentsinTicket.add(commentInTicket);
                }
                object.put("Comments",commentsinTicket);
            }
            jsonObjectList.add(object);
            connection.close();
            return jsonObjectList;
        } else {
            JSONObject jsonObject = new JSONObject();
            List<JSONObject> list = new ArrayList<>();
            jsonObject.put("Error", "Not Exists in for ticket");
            list.add(jsonObject);
            return list;
        }
    }
}
