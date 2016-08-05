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

var unknownErrorDialog = null;
$(function() {
  var dialog = new AJS.Dialog({
    width : 550,
    height : 180,
    id : "unknown-error-dialog",
    closeOnOutsideClick : true
  });

  dialog.addHeader("Error");
  dialog
      .addPanel(
          "Panel",
          "<div><p>Unknown error!</p><div style='text-align: center; padding-top: 2em;'><button class='aui-button aui-button-primary' onclick='unknownErrorDialog.hide();'>Ok</button></div>",
          "panel-body");

  $(document).ajaxError(function(event, jqxhr, settings, thrownError) {
    if (jqxhr.status == 500) {
      dialog.show();
    }
  });

  unknownErrorDialog = dialog;
});
