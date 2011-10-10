<?php
$un=$_POST['name'];
$em=$_POST['email'];
$pw=$_POST['password'];
$st=$_POST['salt'];
//connect to the db
$user = 'fyp';
$pswd = 'fyp';
$db = 'fyp';

$conn = mysql_connect( 'localhost', $user, $pswd ) or die ("Unable to connect to mysql: " . mysql_error());

mysql_select_db( $db, $conn );
//run the query to search for the username and password the match
$query = "INSERT INTO user (name, email, password, salt) VALUES ('$un', '$em', '$pw', '$st')";
$result = mysql_query( $query ) or die( "Unable to verify user because : " . mysql_error() );
//this is where the actual verification happens
if ( $result ) {
	// ( $un == “ajay” && $pw == “ajay”)
	echo 1;  // for correct login response
}
else {
	echo 0; // for incorrect login response
}

mysql_close( $conn );
?>
