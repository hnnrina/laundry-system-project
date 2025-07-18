<?php
require_once 'jwt_utils.php';
header('Content-Type: application/json');
include("db.php");

$data = json_decode(file_get_contents("php://input"));
$email = $data->email ?? '';
$password = $data->password ?? '';

$response = [];

if (!empty($email) && !empty($password)) {
    $stmt = $conn->prepare("SELECT id, name, password FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $storedHash = $row['password'];
        $response["debug_password_entered"] = $password;
        $response["debug_password_stored"] = $storedHash;
        $response["debug_verify_result"] = password_verify($password, $storedHash);

        if (password_verify($password, $storedHash)) {
		$token = generateJWT($row["id"], 'customer');
		$response["success"] = true;
		$response["id"] = $row["id"];
		$response["name"] = $row["name"];
		$response["token"] = $token;
		} else {

            $response["success"] = false;
            $response["message"] = "Invalid password.";
        }
    } else {
        $response["success"] = false;
        // $response["message"] = "User not found.";
    }

    $stmt->close();
} else {
    $response["success"] = false;
    $response["message"] = "Missing email or password.";
}

$conn->close();
echo json_encode($response);