package com.google.sps.data;

/** A post on messageboard. */
public final class Post {
  private final long id;
  private final String name;
  private final String text;
  private final long timestamp;
  private final String imgUrl;

  public Post(long id, String name, String text, long timestamp, String imgUrl) {
    this.id = id;
    this.name = name;
    this.text = text;
    this.timestamp = timestamp;
    this.imgUrl = imgUrl;
  }
}

