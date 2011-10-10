<?php
mysql_connect("host","fyp","fyp");
mysql_select_db("fyp");
 
$q=mysql_query("SELECT * FROM user WHERE id='".$_REQUEST['id']."'");
while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
 
mysql_close();
?>