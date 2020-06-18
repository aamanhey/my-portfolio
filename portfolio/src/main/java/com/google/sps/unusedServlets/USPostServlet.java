// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps.servlets;

// import com.google.appengine.tools.cloudstorage.GcsFileOptions;
// import com.google.appengine.tools.cloudstorage.GcsFilename;
// import com.google.appengine.tools.cloudstorage.GcsInputChannel;
// import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
// import com.google.appengine.tools.cloudstorage.GcsService;
// import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
// import com.google.appengine.tools.cloudstorage.RetryParams;

// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.PreparedQuery;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.api.datastore.Query.SortDirection;

// import com.google.appengine.api.blobstore.BlobKey;
// import com.google.appengine.api.blobstore.BlobstoreService;
// import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

// import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.io.IOException;
// import java.util.Random;

// import com.google.sps.data.Comment;
// import com.google.gson.Gson;

// /** Servlet that uploads and serves posts from blobstore */
// @WebServlet("/uspost")
// public class USPostServlet extends HttpServlet {
//   private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

// //   @Override
// //   public void doGet(HttpServletRequest request, HttpServletResponse response) throws
// IOException {
// //     GcsFilename fileName = getFileName(req);
// //     if (SERVE_USING_BLOBSTORE_API) {//given by magic (from js)
// //         BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
// //         BlobKey blobKey = blobstoreService.createGsBlobKey(
// //             "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
// //         blobstoreService.serve(blobKey, resp);
// //     } else {
// //       GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0,
// BUFFER_SIZE);
// //       copy(Channels.newInputStream(readChannel), resp.getOutputStream());
// //     }
// //     //prefetching buffers data in memory and prefetches it before it is required to avoid
// blocking on the read call
// //     }

//   @Override
//   public void doPost(HttpServletRequest request, HttpServletResponse response) throws
//   ServletException, IOException{
//     GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
//     GcsFilename fileName = new GcsFilename(createFileName());
//     GcsOutputChannel outputChannel;
//     outputChannel = gcsService.createOrReplace(fileName, instance); //writes new file in cloud
//     storage or overwrites if already exists copy(req.getInputStream(),
//     Channels.newOutputStream(outputChannel));
//   }

//   private String createFileName(){
//     //returns key specificate to the file, different from datastore key
//     //datastore stores all the keys as one arrayList
//     //DOWNSIDE - If the arrayList is deleted then cannot guarentee unique keys, will need to go
//     through files and create new one Query query = new
//     Query("FileKeys").addSort("timestamp",SortDirection.DESCENDING); DatastoreService datastore =
//     DatastoreServiceFactory.getDatastoreService(); PreparedQuery results =
//     datastore.prepare(query);

//     Entity file_keys_list = results.asSingleEntity();

//     Integer newKey;
//     ArrayList<int> allkeys = new ArrayList<int>();
//     if(file_keys_list == null){
//         file_keys_list = new Entity("FileKeys");
//         Random rand = new Random();
//         newKey = rand.nextInt(1000);
//         allkeys.add(newKey);
//     }else{
//         newKey = allkeys.get(allkeys.size()-1) + 100;
//     }

//     postEntity.setProperty("filekeys",allkeys);
//     datastore.put(commentEntity);

//     return Integer.toString(newKey);
//   }

//   private static String convertToJson(ArrayList<Comment> messages) {
//         Gson gson = new Gson();
//         return gson.toJson(messages);
//     }
// }
