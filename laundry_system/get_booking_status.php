<?php
require_once 'db.php';
require_once 'jwt_utils.php';

header('Content-Type: application/json');

$headers = getallheaders();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Authorization header missing']);
    exit;
}

$authHeader = $headers['Authorization'];
$token = str_replace('Bearer ', '', $authHeader);
$decoded = validateJWT($token);

if (!$decoded) {
    http_response_code(401);
    echo json_encode(['error' => 'Invalid token']);
    exit;
}

$user_id = $decoded['uid'];
$status = $_POST['status'] ?? 'All'; 

try {
    if ($status === "All") {
       
        $stmt = $conn->prepare("
            SELECT o.id, o.weight_kg, o.time_slot, o.notes, o.status, s.name 
            FROM laundry_orders o 
            JOIN services s ON o.service_id = s.id 
            WHERE o.user_id = ? AND DATE(o.time_slot) >= CURDATE()
        ");
        $stmt->bind_param("i", $user_id);
    } else {
       
        $stmt = $conn->prepare("
            SELECT o.id, o.weight_kg, o.time_slot, o.notes, o.status, s.name 
            FROM laundry_orders o 
            JOIN services s ON o.service_id = s.id 
            WHERE o.user_id = ? AND o.status = ? AND DATE(o.time_slot) >= CURDATE()
        ");
        $stmt->bind_param("is", $user_id, $status); 
    }

    $stmt->execute();
    $result = $stmt->get_result();

    $orders = [];
    while ($row = $result->fetch_assoc()) {
        $orders[] = $row;
    }

    echo json_encode(['success' => true, 'orders' => $orders]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>