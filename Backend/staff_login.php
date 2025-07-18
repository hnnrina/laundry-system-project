<?php
header("Content-Type: application/json");
include("db.php");

$data = json_decode(file_get_contents("php://input"));

$username = $data->username ?? '';
$password = $data->password ?? '';

$response = [];

if ($username && $password) {
    $stmt = $conn->prepare("SELECT staffID, password FROM staff WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $storedHash = $row['password'];
        if (password_verify($password, $storedHash)) {
            $response["success"] = true;
            $response["staff_id"] = $row["staffID"];
        } else {
            $response["success"] = false;
            $response["message"] = "Invalid password.";
        }
    } else {
        $response["success"] = false;
        $response["message"] = "User not found.";
    }

    $stmt->close();
} else {
    $response["success"] = false;
    $response["message"] = "Missing username or password.";
}

$conn->close();
echo json_encode($response);
?>