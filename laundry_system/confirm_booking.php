<?php
header("Content-Type: application/json"); // Force JSON response
include("config/db.php");

$name = $_POST['name'] ?? '';
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';
$phone = $_POST['phone'] ?? '';
$address = $_POST['address'] ?? '';

$response = [];

if ($name && $email && $password && $phone && $address) {
    $stmt = $conn->prepare("INSERT INTO customers (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("sssss", $name, $email, $password, $phone, $address);

    if ($stmt->execute()) {
        $response = ["status" => "success", "message" => "User registered"];
    } else {
        $response = ["status" => "error", "message" => "DB insert failed"];
    }

    $stmt->close();
} else {
    $response = ["status" => "error", "message" => "Missing parameters"];
}

$conn->close();
echo json_encode($response);
?>