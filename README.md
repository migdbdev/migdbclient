<h1>MIGDB-CLIENT-APPLICATION</h1>
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

---------------------------- Regarding Application Related Filepaths -------------------------------
There is a enum called 'FilePath' under the 'org.migdb.migdbclient.config' package. All the application regarding file paths are mentioned in there

Ex : As a example if you need database structure json file you can follow this way. FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTURE.getPath();
