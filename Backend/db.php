<?php
$host = 'localhost';
$db   = 'laundry_system'; // your DB name
$user = 'root';           // default for XAMPP
$pass = '';               // default for XAMPP (no password)

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>