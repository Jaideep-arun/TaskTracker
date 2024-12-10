package com.example.TaskTracker.Routes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.TaskTracker.Models.Ticket_Model;
import com.example.TaskTracker.MySQL.MySQLdataSourceConfig;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/IT")
public class ITRoutes {

    @Autowired
    DataSource dataSource;

    private Connection connection = null;
    private PreparedStatement statement = null;

    @GetMapping("/HelloWorld")
    public String HelloWorld() {
        return "Hi World";
    }
    @GetMapping("/FetchTickets")
    public List<JSONObject> FetchTickets(){


        try {

            connection = dataSource.getConnection();
            statement = connection.prepareStatement("select * from tickets");
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
                    object.put("Link","Add link later");


                    jsonObjectList.add(object);
                }
                return jsonObjectList;
            }

            else
                return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/FetchTickets/ticket")
    public List<JSONObject> FetchTicketsDetail(@RequestParam String ticket_id){

        PreparedStatement CommentStatement = null;
        try {

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
                object.put("Ticket_id", data.getString("ticket_id"));
                object.put("category_id", data.getString("category_id"));
                object.put("sub_category_id", data.getString("sub_category_id"));
                object.put("subject", data.getString("subject"));
                object.put("description",data.getString("description"));
                object.put("priority_id", data.getString("priority_id"));
                object.put("status_id", data.getString("status_id"));
                object.put("assignee_Id", data.getString("assignee_Id"));
                object.put("reported_Id", data.getString("reported_Id"));
                object.put("create_datetime", data.getString("create_datetime"));
                object.put("last_modified_datetime", data.getString("last_modified_datetime"));
                object.put("Link","Add link later");

                if(comments.isBeforeFirst()){
                    String commentsForTicket = "";
                    while (comments.next()){
                        commentsForTicket = commentsForTicket.concat(comments.getString("message"));
                    }
                    object.put("Comments",commentsForTicket);
                }
                jsonObjectList.add(object);
                return jsonObjectList;
            }

            else
                return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/SetAssignee")
    public String setAssignee(@RequestParam String  ticket_id, String admin_id ) throws SQLException {
        connection = dataSource.getConnection();

//        String query = "select * from tickets where ticket_id ='" + json.get("ticket_id") +"'";
//        UPDATE tickets SET assignee_Id='Admin2' WHERE ticket_id = 'T1_2024';
        String query = "UPDATE tickets SET assignee_id='" +admin_id +"', last_modified_datetime = CURRENT_TIMESTAMP WHERE ticket_id = '"+ ticket_id +"'";
        statement = connection.prepareStatement(query);
        int result = statement.executeUpdate() ;
        return "Assignee Updated "+ result;
    }
    @GetMapping("/changeStatus")
    public String changeStatsu(@RequestParam String  ticket_id, String status_id ) throws SQLException {
        connection = dataSource.getConnection();

        String query = "UPDATE tickets SET status_id='" +status_id +"', last_modified_datetime = CURRENT_TIMESTAMP WHERE ticket_id = '"+ ticket_id +"'";
        statement = connection.prepareStatement(query);
        ResultSet fetchStatus = connection.prepareStatement("select status_id from tickets where ticket_id='"+ticket_id+"'").executeQuery();
        fetchStatus.next();
        int result = statement.executeUpdate() ;
        if (result == 1)
            return "Status Updated from "+ fetchStatus.getString("status_id")+" to "+status_id;
        else
            return "Unable to Update";
    }
    @GetMapping("/ticket/addComment")
    public String addComment(@RequestParam String  ticket_id, String user_id ,@RequestBody String comment ) throws SQLException {
        connection = dataSource.getConnection();
//        INSERT INTO comments VALUES
        //    ('com1','T1_2024','Admin1','A new Laptop will be given by tomorrow');
        ResultSet fetchStatus = connection.prepareStatement("select count(message) from comments where ticket_id='"+ticket_id+"'").executeQuery();
        fetchStatus.next();
        int val = fetchStatus.getInt("count(message)");
        String query = "INSERT INTO comments VALUES('"+ticket_id+"_comm_"+ fetchStatus.getInt("count(message)")+1 +"','"+ ticket_id +"','"+user_id+"','"+comment+"')";
        statement = connection.prepareStatement(query);
        statement.execute() ;

        return "Comment Added Successfully to ticket "+ticket_id;

    }

}
