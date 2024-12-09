package com.example.TaskTracker.Routes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/HelloWorld")
    public String HelloWorld() {
        return "Hi World";
    }
    @GetMapping("/FetchTickets")
    public List<JSONObject> FetchTickets(){

        Connection connection = null;
        PreparedStatement statement = null;
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
}
