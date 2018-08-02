<?php
$username = $_POST['username'];
$pwd = $_POST['password'];
$data = "";
$link = mysqli_connect('localhost', 'fajar', 'p@ssw0rd') or die('Cannot connect to the DB');
mysqli_select_db($link, 'db_lokasi')  or die("Could not select examples"); 
$query = "SELECT number FROM callcenter";
$result =  mysqli_query($link,$query) or die('Errorquery: '.$query);
$rows = array();
while ($r = mysqli_fetch_assoc($result)) {
$data = $r['number'];
}
if ($data <> ""){
//$data = "sukses";
echo $data;
}
else{
echo "failed";
//echo $data;
}
?>