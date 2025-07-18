

<?php
require_once 'jwt_utils.php';

$headers = apache_request_headers();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Missing token"]);
    exit;
}

$token = str_replace('Bearer ', '', $headers['Authorization']);
$decoded = validateJWT($token);

if (!$decoded) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Invalid token"]);
    exit;
}

include("db.php");

$id = intval($_GET['id']);
$sql = "SELECT price_per_kg FROM services WHERE id = $id LIMIT 1";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    echo json_encode(["price_per_kg" => floatval($row["price_per_kg"])]);
} else {
    echo json_encode(["error" => "No price found in database."]);
}

$conn->close();
?>