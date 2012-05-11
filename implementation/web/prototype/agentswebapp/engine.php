<?php
include 'logger.php';

//LOG REQUEST
$log = new Logging();  
$log->logREQUEST();

$connection = mysql_connect("localhost","np33566_agendid","agent007");
if (!$connection){
   die('Could not connect: ' . mysql_error());
}
mysql_select_db("np33566_agendid");


$action = $_GET['action'];
switch($action) {
	case 'test':
		echo 'Your test request was successful!';
		break;
	case 'jsontest':
		$paramObj = $_REQUEST['action'];
		$val = array("randomNumber" => 12,
             "randomString" => "ke ke ke",
			 "requestString" => $paramObj
        );
		echo json_encode($val); 
		break;
	case 'dbtest':
		$log->lwrite('test '.$res);
		$res = mysql_query("INSERT INTO `test_table` VALUES ('', 'dbtest');");
		echo $res;
		break;
}
?>