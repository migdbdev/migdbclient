<h1> NOTIFICATION HANDLING </h1>
<p> There is a java class called MigDBNotifier under the utils package. And there is a two constructors available each for custom notification and defined notification. This notification class referes Tray Tester.jar. The jar was added into the project. The referencing link is <a href"https://github.com/PlusHaze/TrayNotification">https://github.com/PlusHaze/TrayNotification</a> .</p>
<p> Sample code is in client application under the SetupNewDBConnectionController.java in line #188 </p>

<h1> JavaFX Dialogue Box </h1>
<p>Refer link provided <a href = "http://code.makery.ch/blog/javafx-dialogs-official/">Click here</a> for adding JavaFX dialogue boxes</p>

<h1>  URL FOR THE GITHUB PAGE </h1>
<p><a href="http://migdbdev.github.io/migdbclient/">http://migdbdev.github.io/migdbclient/</a></p>

<h1>MIGDB-CLIENT-APPLICATION</h1>


---------------------------- Regarding Application Related Filepaths -------------------------------
There is a enum called 'FilePath' under the 'org.migdb.migdbclient.config' package. All the application regarding file paths are mentioned in there

Ex : As a example if you need database structure json file you can follow this way. FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTURE.getPath();
