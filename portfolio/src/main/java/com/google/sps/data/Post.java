package com.google.sps.data;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;

/** A post on messageboard. */
public final class Post {
  private final long id;
  private final String name;
  private final String text;
  private final long timestamp;
  private final String bucketName;
  private final String objectName;
  private final BlobId imgObjId;
  private final Blob imgfile;

  public Post(long id, String name, String text, long timestamp, String bucketName,
      String objectName, BlobId imgObjId, Blob imgfile) {
    this.id = id;
    this.name = name;
    this.text = text;
    this.timestamp = timestamp;
    this.bucketName = bucketName;
    this.objectName = objectName;
    this.imgObjId = imgObjId;
    this.imgfile = imgfile;
  }
}