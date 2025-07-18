<?php
header('Content-Type: application/json');
include("db.php");

ob_clean();

$sql = "SELECT o.*, u.address 
        FROM laundry_orders o
        JOIN users u ON o.user_id = u.id";

$result = $conn->query($sql);
$orders = [];

while ($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

echo json_encode([
    "success" => true,
    "orders" => $orders
]);

$conn->close();
exit;
?>