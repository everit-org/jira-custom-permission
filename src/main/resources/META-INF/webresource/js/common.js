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

if (typeof $ === 'undefined') {
  $ = AJS.$;
}

if (!$.parseHTML) {
  $.parseHTML = function(str) {
    var tmp = document.implementation.createHTMLDocument("Everit Jira HR Admin Plugin");
    tmp.body.innerHTML = str;
    return tmp.body.children;
  }
}

$(function() {
  AJS.$('.aui-date-picker.everit-auto-extend').each(function() {
    new AJS.DatePicker(this, {
      'overrideBrowserDefault' : true
    });
  });
});

var processRuntimeAlerts = function() {
  $('.everit-jira-runtimealert').each(function() {
    var thisObj = $(this);
    var alertType = thisObj.attr('data-everit-jira-alert-type');
    var alertMessage = thisObj.attr('data-everit-jira-alert-message');
    thisObj.remove();
    if (alertType == 'info') {
      AJS.messages.generic({
        body : alertMessage,
        fadeout : true
      });
    } else if (alertType == 'error') {
      AJS.messages.error({
        body : alertMessage,
        fadeout : true
      });
    } else if (alertType == 'warning') {
      AJS.messages.warning({
        body : alertMessage,
        fadeout : true
      });
    }
  });
}

$( document ).ajaxComplete(function( event, xhr, settings ) {
  if (xhr.status == 278) {
  var newLocation = xhr.getResponseHeader("Location");
  if (newLocation) {
    window.location = newLocation;
  }
  }
});

