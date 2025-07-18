

<?php
include("db.php");

$sql = "SELECT laundry_orders.id, users.name, services.name AS service, laundry_orders.status
        FROM laundry_orders
        JOIN users ON laundry_orders.user_id = users.id
        JOIN services ON laundry_orders.service_id = services.id";

$result = $conn->query($sql);
$orders = [];

while ($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

echo json_encode($orders);
?>