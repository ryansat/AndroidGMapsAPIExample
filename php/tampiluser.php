<?php
$link = mysqli_connect('localhost', 'fajar', 'p@ssw0rd') or die('Cannot connect to the DB');
mysqli_select_db($link, 'db_lokasi')  or die("Could not select examples"); 
$query = "SELECT * FROM users";
$result =  mysqli_query($link,$query) or die('Errorquery: '.$query);
$rows = array();
while ($r = mysqli_fetch_assoc($result)) {
$rows[] = $r;
}
$data = "{users:".json_encode($rows)."}";
echo $data;
?>