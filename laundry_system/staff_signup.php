<?php
header("Content-Type: application/json");
include("db.php");

$data = json_decode(file_get_contents("php://input"));

$username = $data->username ?? '';
$password = $data->password ?? '';

$response = [];

if ($username && $password) {
    // ✅ Encrypt the password before storing
    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

    $stmt = $conn->prepare("INSERT INTO staff (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $hashedPassword);

    if ($stmt->execute()) {
        $response["success"] = true;
        $response["message"] = "Staff registered successfully!";
    } else {
        $response["success"] = false;
        $response["message"] = "Database error: " . $conn->error;
    }

    $stmt->close();
} else {
    $response["success"] = false;
    $response["message"] = "Missing fields.";
}

$conn->close();
echo json_encode($response);
?>