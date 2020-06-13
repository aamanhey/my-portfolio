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

package com.google.sps.experimental;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@SuppressWarnings("serial")
@WebServlet("/upload_gcs")
@MultipartConfig()
public class UploadServlet extends HttpServlet {
  private static final String BUCKET_NAME = "adrian_posts_bucket";
  private static Storage storage = null;

  @Override
  public void init() {
    storage = StorageOptions.getDefaultInstance().getService();
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    final Part filePart = req.getPart("file");
    final String fileName = filePart.getSubmittedFileName();
    Blob blob = storage.create(
        BlobInfo.newBuilder(BUCKET_NAME, fileName).build(), filePart.getInputStream());

    resp.sendRedirect("/view_image?file-name=" + fileName);
  }
}
