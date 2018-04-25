package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.LocationSignalPackage;
import play.*;
import play.mvc.*;

import scala.Function1;
import scala.Function2;
import scala.PartialFunction;
import scala.Tuple2;
import scala.collection.*;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.Iterable;
import scala.collection.immutable.LinearSeq;
import scala.collection.immutable.Seq;
import scala.collection.immutable.Traversable;
import scala.util.parsing.json.JSONArray;
import views.html.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result postLocationSignalDetails(){
        JsonNode jsonPacket = request().body().asJson();
        Iterator<JsonNode>  it = jsonPacket.elements();
        ObjectNode responseJson = play.libs.Json.newObject();
        if (it.hasNext()){
            JsonNode a = it.next();
            System.out.println(a.get("SSID"));
          //  LocationSignalPackage locationSignalPackage = play.libs.Json.fromJson(jsonPacket,LocationSignalPackage.class);
            Connection conn = connect();
            try {
                System.out.println("Inside the message web service");
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO LocationSignal(SSID, Lat, Long, Rssi) VALUES(?,?,?,?); ");
                pstmt.setString(1, a.get("SSID").toString());
                pstmt.setString(2, a.get("Lat").toString());
                pstmt.setString(3, a.get("Long").toString());
                pstmt.setString(4, a.get("Rssi").toString());
                int statusCode = pstmt.executeUpdate();
                System.out.println("Inserted content into table");
                System.out.println(statusCode);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            responseJson.put("success","The content has been saved");
        }
        else {
            responseJson.put("error","No content found");
        }


        return ok(responseJson);
    }
    public Result getLocationDetails(){
        Connection conn = connect();
        ObjectNode responseJson = play.libs.Json.newObject();
        String output ="";
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM LocationSignal;");
            ResultSet rs= pstmt.executeQuery();

            while (rs.next()){

                output += rs.getLong(1)+",";
                for(int i=2;i<=5;i++){
                    output += rs.getString(i)+",";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(output.length()>0)
            responseJson.put("output",output.substring(0,output.length()-1));
        else
            responseJson.put("output","");
        return ok(responseJson);
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:LocationSignal.db";

            File file = new File("LocationSignal.db");
            System.out.println(file.exists());
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            return conn;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
