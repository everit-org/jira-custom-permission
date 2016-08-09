/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var assignGlobalPermission = function(event) {
  event.preventDefault();
  
  var formdata = {
      "action" : "assign",
      "permission" : $("#permission-select").prop("value"),
      "group" : $("#group-select").prop("value")
  }
  
  $.ajax({
    url : '#',
    type : 'POST',
    data : formdata
  }).success(function(content) {
    everit.partialresponse.process(content);
    processRuntimeAlerts();
  }).error(function(resp) {
    if (resp.status == 400) {
      everit.partialresponse.process(resp.responseText);
      processRuntimeAlerts();
    }
  });
}

var removeJiraGroupFromGlobalPermission = function(permissionKey, groupId) {
  var formdata = {
      "action" : "remove",
      "permission" : permissionKey,
      "group" : groupId
  }
  
  $.ajax({
    url : '#',
    type : 'POST',
    data : formdata
  }).success(function(content) {
    everit.partialresponse.process(content);
    processRuntimeAlerts();
  }).error(function(resp) {
    if (resp.status == 400) {
      everit.partialresponse.process(resp.responseText);
      processRuntimeAlerts();
    }
  });
}

