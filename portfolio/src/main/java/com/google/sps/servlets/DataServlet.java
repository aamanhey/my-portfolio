// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import com.google.sps.data.Comment;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp",SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    //equal string and check to see if it null
    //if null set equal to default
    String userNum = request.getParameter("user-comment-num");
    int numberComments = 5;
    if (userNum != null) {
        numberComments = Integer.parseInt(userNum);
    }
    int numberLoaded = 0;

    ArrayList<Comment> comments = new ArrayList<Comment>();
    for(Entity entity : results.asIterable()){
        if (numberLoaded < numberComments){
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String text = (String) entity.getProperty("text");
            long timestamp = (long) entity.getProperty("timestamp");

            Comment comment = new Comment(id, name, text, timestamp);
            comments.add(comment);
            numberLoaded += 1;
        }else{
            break;
        }
    }
    
    String jsoncom = convertToJson(comments);

    response.setContentType("application/json;");
    response.getWriter().println(jsoncom);
    }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    String name = request.getParameter("name-input");
    String text = request.getParameter("text-input");
    long timestamp = System.currentTimeMillis();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name",name);
    commentEntity.setProperty("text",text);
    commentEntity.setProperty("timestamp",timestamp);

    datastore.put(commentEntity);

    response.setContentType("text/html;");
    response.sendRedirect("/index.html");
  }

  private static String convertToJson(ArrayList<Comment> messages) {
        Gson gson = new Gson();
        return gson.toJson(messages);
    }
}
