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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['My favorite fruits are mangoes.', 'My favorite vacation type is camping.', 'My favorite color is red.', 'I want to join the Peace Corps after college.'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function getContent() {
    fetchBlob();
    getPosts();
    getComments();
}

/*comments functionality*/
function deleteAllComments(){
    confirm("Are you sure you want to delete all comments?");
    fetch('/delete-data',{method:"POST"}).then(response => response.json()).then((emptyComments) => {
    });
    getComments();
}

function deleteComment(){
    fetch('/delete-data?comment-key='+getCommentKey()).then(response => response.json()).then((emptyComments) => {
    });
    getComments();
}

function getCommentKey(){
    let searchParams = (new URL(document.location)).searchParams;
    let commentkey = searchParams.get("comment-key");
    if(commentkey == null || commentkey.length === 0){
        return "1";
    }
    return commentkey;
}

function getComments(){
   fetch('/data?user-comment-num='+getUserNum()).then(response => response.json()).then((comments) => {
    //need to have the ?user-comment-num in order to pass correct params
    const messageBoard = document.getElementById('comments-container');
    comments.forEach((comment) => {
      messageBoard.appendChild(createCmtEl(comment));
    })   });
}

function getUserNum(){
    //the current document's URL params
    let searchParams = (new URL(document.location)).searchParams;
    //that returns a map like structure, so we can get out desired param with .get()
    let userNum = searchParams.get("user-comment-num");
    if(userNum == null || userNum.length === 0){
        return "1";
    }
    return userNum;
}

function createCmtEl(comment) {
  const comElem = document.createElement('li');
  comElem.className = 'Comment';

  const nameElem = document.createElement('span');
  nameElem.innerText = comment.name;

  const txtElem = document.createElement('span');
  var str = " commented: \n " + comment.text;
  txtElem.innerText = str;

  comElem.appendChild(nameElem);
  comElem.appendChild(txtElem);
  return comElem;
}

/* Post functionality */
function getPostKey(){
    let searchParams = (new URL(document.location)).searchParams;
    let userNum = searchParams.get("post-key");
    if(postkey == null || postkey.length === 0){
        return "1";
    }
    return postkey;
}

function getPosts(){
   fetch('/get-posts').then(response => response.json()).then((posts) => {
    const messageBoard = document.getElementById('posts-container');
    posts.forEach((post) => {
      messageBoard.appendChild(createCustomPostEl(post));
    })   });
}

function fetchBlob() {
  fetch('/blobstore-upload-url').then((response) => {return response.text();}).then((imageUploadUrl) => {
      const messageForm = document.getElementById('createPost');//this sets the action
      messageForm.action = imageUploadUrl;//of the form to the blobstore url
      });
}

function createCustomPostEl(post) {
  const postElem = document.createElement('ul');
  postElem.className = 'Post';

  const section = document.createElement('div');
  section.className = 'topic';
  section.style.border = "1px dotted black";

  const profileHandler = document.createElement('div');
  const nameElem = document.createElement('span');
  nameElem.innerText = post.name;
  nameElem.style.fontSize = "20px";
  profileHandler.appendChild(nameElem);
  
  const textHandler = document.createElement('div');
  const txtElem = document.createElement('span');
  var str = " posted: \n " + post.text;
  txtElem.innerText = str;
  txtElem.style.fontSize = "16px";
  textHandler.appendChild(txtElem);

  
  const imageHandler = document.createElement('div');
  const imgElem = document.createElement('img');
  imgElem.src = post.imgUrl;
  imgElem.style.width = "25%";
  imgElem.style.height= "auto";
  imageHandler.appendChild(imgElem);

  postElem.appendChild(profileHandler);
  postElem.appendChild(textHandler);
  postElem.appendChild(imageHandler);

  section.appendChild(postElem);
  return section;
}

