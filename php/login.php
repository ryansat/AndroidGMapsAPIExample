<?php
$username = $_POST['username'];
$pwd = $_POST['password'];
$data = "";
$link = mysqli_connect('localhost', 'fajar', 'p@ssw0rd') or die('Cannot connect to the DB');
mysqli_select_db($link, 'db_lokasi')  or die("Could not select examples"); 
$query = "SELECT * FROM users where username='".$username."' and password='".$pwd."'";
$result =  mysqli_query($link,$query) or die('Errorquery: '.$query);
$rows = array();
while ($r = mysqli_fetch_assoc($result)) {
$data = $r['userid'];
}
if ($data <> ""){
echo "login success";
header("Location: http://satriaworlds.net/maps/cobamaps.php");
exit;
}
else{
echo "login failed";
//echo $data;
}
?>