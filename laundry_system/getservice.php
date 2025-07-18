

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

$sql = "SELECT id, name, description, price_per_kg FROM services";
$result = $conn->query($sql);

$services = [];
while ($row = $result->fetch_assoc()) {
    $services[] = $row;
}

echo json_encode($services);
?>