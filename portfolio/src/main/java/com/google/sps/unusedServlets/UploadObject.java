package com.google.cloud.examples.storage.objects;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.sps.data.Post;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/* Uploads files to storage bucket*/
@WebServlet("/post-data")
@MultipartConfig(maxFileSize = 5242880, // 5 MB
    maxRequestSize = 20971520) // 20 MB
public class UploadObject extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Post").addSort("timeStamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    Storage storage =
        StorageOptions.newBuilder().setProjectId("amanhey-step-2020").build().getService();

    // a blob is a large binary object stored as a column value in a row of a database table

    ArrayList<Post> posts = new ArrayList<Post>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String text = (String) entity.getProperty("text");
      long timeStamp = (long) entity.getProperty("timeStamp");
      // String imgUrl = (String) entity.getProperty("imgUrl");
      // Post post = new Post(id, name, text, timeStamp, bucketName, objectName, blobId, imgFile);
      // if(imgUrl == null){
      String bucketName = (String) entity.getProperty("bucketName");
      String objectName = (String) entity.getProperty("objectName");
      BlobId blobId = (BlobId) entity.getProperty("imgObjId");
      Blob imgFile = (Blob) storage.get(BlobId.of(bucketName, objectName));
      //     post = new Post(id, name, text, timeStamp, bucketName, objectName, blobId, imgFile);
      // }else{
      //     post = new Post(id, name, text, timeStamp, imgUrl);
      // }
      Post post = new Post(id, name, text, timeStamp, bucketName, objectName, blobId, imgFile);
      posts.add(post);
    }

    String jsonComments = convertToJson(posts);

    response.setContentType("application/json;");
    response.getWriter().println(jsonComments);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // when using multipart/form-data requests te form data pars are not availabe as request params
    // the are only available as form data parts by request.getPart()
    String name = request.getParameter("name-input");
    String imgFilePath = request.getParameter("img-input-file");
    String imgUrl = request.getParameter("img-input-url");
    String text = request.getParameter("text-input");
    long timeStamp = System.currentTimeMillis();

    boolean useStorageBucket = false;
    if (imgUrl == null) {
      useStorageBucket = true;
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity newPost = new Entity("Post");
    newPost.setProperty("name", name);
    newPost.setProperty("text", text);
    newPost.setProperty("time-stamp", timeStamp);
    // if(useStorageBucket){
    // The ID of your GCP project
    String projectId = "amanhey-step-2020";

    // The ID of your GCS bucket
    String bucketName = "adrian_posts_bucket";

    // The ID of your GCS object
    String objectName = createFileName();

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    // The Storage class creates an instance to interface the Google Cloud Storage
    //
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
    System.out.println("This is the filepath: " + imgFilePath);
    if (isGoodFile(imgFilePath)) {
      storage.create(blobInfo, Files.readAllBytes(Paths.get(imgFilePath)));
    } else {
      throw new FileNotFoundException();
    }
    newPost.setProperty("bucketName", bucketName);
    newPost.setProperty("imgObjName", objectName);
    newPost.setProperty("imgObjId", blobId);
    //}else{
    newPost.setProperty("imgUrl", imgUrl);
    //}
    /* ellipsis identifies a variable number of arguments

    The Files class consists exclusively of static methods that operate on files, directories, or
    other types of files. readAllBytes takes in a Path as a param and returns a byte array
        readAllBytes will throws an IOException if an I/O error occurs reading from the stream or an
    OutOfMemoryError if an array of the required size cannot be allocated

    Path is an object that may be used to locate a file in a file system. It will typically
    represent a system dependent file path. !!Different from Paths class Paths.get converts a path
    string to a Path


    Storage create method has two applicable variations:
        Creates a new blob with the sub array of the given byte array.
            create(BlobInfo blobInfo, byte[] content, int offset, int length,
    Storage.BlobTargetOption... options) Creates a new blob. create(BlobInfo blobInfo, byte[]
    content, Storage.BlobTargetOption... options)

    the BlobInfo is an object that holds information about an object in Google Cloud Storage
        -includes BlobId instance and the set of properties (e.g. blob's access control
    configuration, user provided metadata, etc.) -Instances of this class are used to create a new
    object in Google Cloud Storage or update the properties of an existing object

    */
    datastore.put(newPost);

    response.setContentType("text/html;");
    response.sendRedirect("/index.html");
  }

  private boolean isGoodFile(String imgFilePath) {
    if (Paths.get(imgFilePath) instanceof Path && Files.isReadable(Paths.get(imgFilePath))) {
      if (Paths.get(imgFilePath).toString() == imgFilePath) {
        return true;
      }
    }
    return false;
  }

  private String createFileName() {
    // returns key specificate to the file, different from datastore key
    // datastore stores all the keys as one arrayList
    // DOWNSIDE - If the arrayList is deleted then cannot guarentee unique keys, will need to go
    // through files and create new one
    Query query = new Query("FileKeys").addSort("timeStamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    Entity fileKeysList = results.asSingleEntity();

    Integer newKey;
    ArrayList<Integer> allKeys = new ArrayList<Integer>();
    if (fileKeysList == null) {
      fileKeysList = new Entity("FileKeys");
      Random rand = new Random();
      newKey = rand.nextInt(1000);
      allKeys.add(newKey);
    } else {
      newKey = allKeys.get(allKeys.size() - 1) + 100;
    }
    Entity postEntity = new Entity("Post");
    postEntity.setProperty("filekeys", allKeys);
    datastore.put(postEntity);

    return Integer.toString(newKey);
  }

  private static String convertToJson(ArrayList<Post> messages) {
    Gson gson = new Gson();
    return gson.toJson(messages);
  }
}