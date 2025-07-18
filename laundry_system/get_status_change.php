<?php
header('Content-Type: application/json');
require 'db.php';

if (!isset($_GET['order_id'])) {
    echo json_encode(['success' => false, 'message' => 'Order ID is required']);
    exit;
}

$order_id = $_GET['order_id'];

$sql = "SELECT sc.new_status, sc.changed_at, s.username AS staff_name
        FROM status_change sc
        JOIN staff s ON sc.staff_id = s.staffID
        WHERE sc.order_id = ?
        ORDER BY sc.changed_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $order_id);
$stmt->execute();
$result = $stmt->get_result();

$changes = [];
while ($row = $result->fetch_assoc()) {
    $changes[] = $row;
}

echo json_encode(['success' => true, 'changes' => $changes]);
?>
