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
import com.google.sps.data.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns stores and retrieves posts from datastore. */
@WebServlet("/get-posts")
public class BlobstoreRetrieveServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Post").addSort("timeStamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    int numberPosts = 5;
    int numberLoaded = 0;

    ArrayList<Post> posts = new ArrayList<Post>();
    for (Entity entity : results.asIterable()) {
      if (numberLoaded < numberPosts) {
        String imageUrl = (String) entity.getProperty("imageUrl");
        if(imageUrl != null && !(imageUrl.equals(""))){
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String text = (String) entity.getProperty("text");
            long timeStamp = (long) entity.getProperty("timeStamp");

            Post post = new Post(id, name, text, timeStamp, imageUrl);
            posts.add(post);
            numberLoaded++;
            }
      }else {
        break;
      }
    }

    String jsonPosts = convertToJson(posts);

    response.setContentType("application/json;");
    response.getWriter().println(jsonPosts);
  }

  private static String convertToJson(ArrayList<Post> messages) {
    Gson gson = new Gson();
    return gson.toJson(messages);
  }
}
