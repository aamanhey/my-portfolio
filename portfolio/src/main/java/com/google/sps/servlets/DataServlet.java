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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    ArrayList<String> comments = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = convertToJson(comments);

    response.setContentType("text/html;");//going to priint directly on page
    response.getWriter().println(json);
    }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
    String name = request.getParameter("name-input");
    String text = request.getParameter("text-input");
    long timestamp = System.currentTimeMillis();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name",name);
    commentEntity.setProperty("text",text);

    datastore.put(commentEntity);

    comments.add(text);
    response.setContentType("text/html;");
    //response.getWriter().println(comments);
    response.sendRedirect("/index.html");
  }

  private static String convertToJson(ArrayList<String> messages) {
        Gson gson = new Gson();
        return gson.toJson(messages);
    }

  private String convertToJsonW(ArrayList<String> list){
    String json = "['";
    int i;
    for(i=0;i<list.size();i++){
      json += "{'Comment "+i+"' : '";
      if(i == list.size() - 1){
        json += list.get(i) + "}";
      }else{
        json += list.get(i) + "},";
    }
    }
    json += "]";
    return json;
  }
}
