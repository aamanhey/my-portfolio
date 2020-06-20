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
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns stores and retrieves comments from datastore. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  static final String IMAGEURL_PARAM = "img-input";
  static final String NAME_PARAM = "name-input";
  static final String TEXT_PARAM = "text-input";
  static final String COMMENT_AMOUNT_PARAM = "user-comment-num";

  static final String IMAGEURL_PROPERTY = "imageUrl";
  static final String NAME_PROPERTY = "name";
  static final String TEXT_PROPERTY = "text";
  static final String TIMESTAMP_PROPERTY = "timeStamp";

  static final String ENTITY_KIND = "Comment";
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(ENTITY_KIND).addSort(TIMESTAMP_PROPERTY, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    // equal string and check to see if it null
    int numberLoaded = 0;
    String userNum = request.getParameter(COMMENT_AMOUNT_PARAM);
    int numberComments = 5;
    if (userNum != null) {
      numberComments = Integer.parseInt(userNum);
    }

    ArrayList<Comment> comments = new ArrayList<Comment>();
    for (Entity entity : results.asIterable()) {
      if (numberLoaded < numberComments) {
        long id = entity.getKey().getId();
        String name = (String) entity.getProperty(NAME_PROPERTY);
        String text = (String) entity.getProperty(TEXT_PROPERTY);
        long timeStamp = (long) entity.getProperty(TIMESTAMP_PROPERTY);

        Comment comment = new Comment(id, name, text, timeStamp);
        comments.add(comment);
        numberLoaded++;
      } else {
        break;
      }
    }

    String jsonComments = convertToJson(comments);

    response.setContentType("application/json;");
    response.getWriter().println(jsonComments);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter(NAME_PARAM);
    String text = request.getParameter(TEXT_PARAM);
    long timeStamp = System.currentTimeMillis();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity commentEntity = new Entity(ENTITY_KIND);
    commentEntity.setProperty(NAME_PROPERTY, name);
    commentEntity.setProperty(TEXT_PROPERTY, text);
    commentEntity.setProperty(TIMESTAMP_PROPERTY, timeStamp);

    datastore.put(commentEntity);

    response.setContentType("text/html;");
    response.sendRedirect("/index.html");
  }

  private static String convertToJson(ArrayList<Comment> messages) {
    Gson gson = new Gson();
    return gson.toJson(messages);
  }
}

