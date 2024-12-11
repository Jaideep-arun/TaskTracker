package com.example.TaskTracker.Routes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public List<JSONObject> FetchTickets() throws SQLException {
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
                object.put("Link","http://localhost:8080/IT/FetchTickets/ticket?ticket_id="+data.getString("ticket_id"));
                jsonObjectList.add(object);
            }
            connection.close();
            return jsonObjectList;
        } else {
            JSONObject jsonObject = new JSONObject();
            List<JSONObject> list = new ArrayList<>();
            jsonObject.put("Error", "Not Exists blabla");
            list.add(jsonObject);
            return list;
        }
    }

    @GetMapping("/FetchTickets/ticket")
    public List<JSONObject> FetchTicketsDetail(@RequestParam String ticket_id) throws SQLException {
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

    @GetMapping("/SetAssignee")
    public String setAssignee(@RequestParam String  ticket_id, String admin_id ) throws SQLException {
        connection = dataSource.getConnection();
        String query = "UPDATE tickets SET assignee_id='" +admin_id +"', last_modified_datetime = CURRENT_TIMESTAMP WHERE ticket_id = '"+ ticket_id +"'";
        statement = connection.prepareStatement(query);
        int result = statement.executeUpdate() ;
        connection.close();
        if (result == 1)
            return "Assignee Updated ";
        else
            return "Assignee not Updated";
    }


    @GetMapping("/changeStatus")
    public String changeStatus(@RequestParam String  ticket_id, String status_id ) throws SQLException {

        String query = "UPDATE tickets SET status_id='" +status_id +"', last_modified_datetime = CURRENT_TIMESTAMP WHERE ticket_id = '"+ ticket_id +"'";

        connection = dataSource.getConnection();
        statement = connection.prepareStatement(query);
        ResultSet fetchStatus = connection.prepareStatement("select status_id from tickets where ticket_id='"+ticket_id+"'").executeQuery();
        fetchStatus.next();
        int result = statement.executeUpdate() ;
        connection.close();
        if (result == 1)
            return "Status Updated from "+ fetchStatus.getString("status_id")+" to "+status_id;
        else
            return "Unable to Update";
    }


    @PostMapping("/ticket/addComment")
    public String addComment(@RequestParam String  ticket_id, String user_id ,@RequestBody String comment ) throws SQLException {
        connection = dataSource.getConnection();
        int exists = connection.prepareStatement("select count(*) from Admin_team where admin_id='"+user_id).executeUpdate();
        if (exists == 1) {
            ResultSet fetchStatus =
                connection.prepareStatement("select count(message) from comments where ticket_id='" + ticket_id + "'")
                    .executeQuery();
            fetchStatus.next();
            int val = fetchStatus.getInt("count(message)") + 1;
            String query =
                "INSERT INTO comments VALUES('" + ticket_id + "_comm_" + val + "','" + ticket_id + "','" + user_id + "','" + comment + "')";
            statement = connection.prepareStatement(query);
            int res = statement.executeUpdate();
            connection.close();

            if (res == 1)
                return "Comment Added Successfully to ticket " + ticket_id;
            else
                return "Unable to Add comment";
        } else {
            return "User with admin id "+user_id+" does not exist";
        }
    }

}
