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

async function getContent() {
  const response = await fetch('/data');
  const message = await response.text();
  document.getElementById('message-container').innerText = message;
}

function getComments(){
   fetch('/data').then(response => response.json()).then((comments) => {
    console.log(comments);
    const messageBoard = document.getElementById('comments-container');
    comments.forEach((comment) => {
      messageBoard.appendChild(createCmtEl(comment));
    })    
  });
}

function createCmtEl(comment) {
  const comElem = document.createElement('li');
  comElem.className = 'Comment';

  const nameElem = document.createElement('span');
  nameElem.innerText = comment.name;

  const txtElem = document.createElement('span');
  txtElem.innerText = comment.text;

  comElem.appendChild(nameElem);
  comElem.appendChild(txtElem);
  return comElem;
}
