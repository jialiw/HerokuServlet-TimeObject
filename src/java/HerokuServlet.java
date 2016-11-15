/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mongodb.*;
import java.util.Arrays;
import java.util.Iterator;
import javax.servlet.annotation.WebServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Dan
 */
@WebServlet(name = "HerokuMobileTest", urlPatterns={"/*"})
public class HerokuServlet extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        System.out.println("Console: doGet visited");
        try{
            // connect to the database
            DB db = DBConnection();
                       
            DBCollection user = db.getCollection("user");
            DBCollection activity = db.getCollection("activity");
            DBCollection actor = db.getCollection("actor");
            DBCollection patient = db.getCollection("patient");
            DBCollection patientActorActivity = db.getCollection("patient_actor_activity");
                        
            JSONArray jsonArray = new JSONArray();
            //BasicDBObject[] demoDataList = modifyUserData();
            //users.insert(demoDataList);
            Gson gson = new Gson();
            String queryName = request.getPathInfo().substring(1);
            int actorIndex, activityIndex, patientIndex;
            System.out.println("Path Info: " + queryName);
            
            StringBuilder resultBuilder = new StringBuilder();
            if (queryName != null) {
                BasicDBObject query = new BasicDBObject("paa_id",queryName);
                DBCursor cursor = patientActorActivity.find(query);
                while(cursor.hasNext()){
                    resultBuilder.append(cursor.next());
                }
                actorIndex = resultBuilder.indexOf("actor_id");
                activityIndex = resultBuilder.indexOf("activity_id");
                patientIndex = resultBuilder.indexOf("patient_id");
                out.println("actorIndex: " + actorIndex + "; activityIndex: " + activityIndex + "; patientIndex: " + patientIndex);
                
                String actorSubstring = resultBuilder.toString().substring(actorIndex);
                String activitySubstring = resultBuilder.toString().substring(activityIndex);
                String patientSubstring = resultBuilder.toString().substring(patientIndex);
                
                String patientId = extractString(patientSubstring);
                out.println("patientId: " + patientId);
                String actorId = extractString(actorSubstring);
                out.println("actorId: " + actorId);
                String activityId = extractString(activitySubstring);
                out.println("activityId: " + activityId);
                
                resultBuilder.append(getObject(patientId,"patient"));
                out.println(resultBuilder.toString());
                out.println("--------------------");
                resultBuilder.append(getObject(activityId, "activity"));
                out.println(resultBuilder.toString());
                out.println("--------------------");
                resultBuilder.append(getObject(actorId, "actor"));
                out.println(resultBuilder.toString());
                out.println("--------------------");
                                
                String json = gson.toJson(resultBuilder.toString());
                out.println(resultBuilder.toString());
                out.println();
                out.println();
                response.setStatus(200);
                response.setContentType("application/json");
                out.print(json);
                out.flush();
            } 
//            users.drop();
//            client.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public String extractString (String infoString) {
        String noDoubleQuoteString = infoString.replaceAll("\"", "");
        int start = noDoubleQuoteString.indexOf(":")+1;
        int end = noDoubleQuoteString.indexOf(",");
        String result = noDoubleQuoteString.substring(start, end).trim();
        return result;
    }
    
    public static DB DBConnection() {
        //Connect to MongoDB server 
        MongoClientURI uri = new MongoClientURI("mongodb://user:user@ds149437.mlab.com:49437/capstone");
        MongoCredential credential = MongoCredential.createCredential("user", "user", "user".toCharArray());
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());
        return db;
    }
    
    public StringBuilder getObject (String id, String query ) {
        DB db = DBConnection();
        DBCollection collection = db.getCollection(query);
        String queryName = query + "_id";
        System.out.println("Queryname: " + queryName);
        BasicDBObject queryObject = new BasicDBObject(queryName, id);
        StringBuilder sb = new StringBuilder();
        DBCursor cursor = collection.find(queryObject);
        System.out.println("DBCursor size: " + cursor.size());  
        sb.append("\n");
        while(cursor.hasNext()){
            sb.append(cursor.next());
        }
        return sb;
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,response); 
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
