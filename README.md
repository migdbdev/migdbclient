<h1>MIGDB CLIENT APPLICATION</h1>
<table class="tg">
  <tr>
    <th class="tg-yw4l">Version</th>
    <th class="tg-yw4l">Build Status</th>
  </tr>
  <tr>
    <td class="tg-yw4l">v1.00</td>
    <td class="tg-yw4l">
    <img src="https://travis-ci.org/migdbdev/migdbclient.svg?branch=master"/>
    </td>
  </tr>
</table>
<h2> NOTIFICATION HANDLING </h2>
<p> There is a java class called MigDBNotifier under the utils package. And there is a two constructors available each for custom notification and defined notification. This notification class referes Tray Tester.jar. The jar was added into the project. The referencing link is <a href"https://github.com/PlusHaze/TrayNotification">https://github.com/PlusHaze/TrayNotification</a> .</p>

<p><span style="color:red"> Note </span> : Add tray tester.jar into your classpath</p>

<p> Sample code is in client application under the SetupNewDBConnectionController.java in line #188 </p>

<p>&nbsp;&nbsp;&nbsp;&nbsp;String title = "Attention";</br>
   &nbsp;&nbsp;&nbsp;&nbsp;String message = "Successfully created!";</br>
   &nbsp;&nbsp;&nbsp;&nbsp;AnimationType animationType = AnimationType.FADE;</br>
   &nbsp;&nbsp;&nbsp;&nbsp;NotificationType notificationType = NotificationType.SUCCESS;</br>
   &nbsp;&nbsp;&nbsp;&nbsp;int showTime = 6;</br>
			</br>
   &nbsp;&nbsp;&nbsp;&nbsp;MigDBNotifier notification = new MigDBNotifier(title, message, animationType, notificationType,showTime);</br>
   &nbsp;&nbsp;&nbsp;&nbsp;notification.createDefinedNotification();</p></br>

<h1> JavaFX Dialogue Box </h1>
<p>Refer link provided <a href = "http://code.makery.ch/blog/javafx-dialogs-official/">Click here</a> for adding JavaFX dialogue boxes</p>

<h1>  URL FOR THE GITHUB PAGE </h1>
<p><a href="http://migdbdev.github.io/migdbclient/">http://migdbdev.github.io/migdbclient/</a></p>

<h1>MIGDB-CLIENT-APPLICATION</h1>


---------------------------- Regarding Application Related Filepaths -------------------------------
There is a enum called 'FilePath' under the 'org.migdb.migdbclient.config' package. All the application regarding file paths are mentioned in there

Ex : As a example if you need database structure json file you can follow this way. FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTURE.getPath();
