<?php
ob_start();
header("Content-Type: application/json");
require_once("db.php");

$response = array();

try {
    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['id']) || !isset($data['status']) || !isset($data['staff_id'])) {
        http_response_code(400);
        $response['success'] = false;
        $response['message'] = 'Missing required fields';
        echo json_encode($response);
        ob_end_flush();
        exit;
    }

    $orderId = intval($data['id']);
    $status = $data['status'];
    $staffId = intval($data['staff_id']);

    // 1. Update laundry_orders table
    $updateQuery = "UPDATE laundry_orders SET status = ? WHERE id = ?";
    $stmt = $conn->prepare($updateQuery);
    $stmt->bind_param("si", $status, $orderId);

    if ($stmt->execute()) {
        // 2. Insert into status_change table
        $insertQuery = "INSERT INTO status_change (order_id, staff_id, new_status, changed_at) VALUES (?, ?, ?, NOW())";
        $insertStmt = $conn->prepare($insertQuery);
        $insertStmt->bind_param("iis", $orderId, $staffId, $status);
        $insertStmt->execute();

        $response['success'] = true;
        $response['message'] = 'Order status updated and staff action recorded.';
    } else {
        $response['success'] = false;
        $response['message'] = 'Failed to update order status.';
    }

    echo json_encode($response);
} catch (Exception $e) {
    http_response_code(500);
    $response['success'] = false;
    $response['message'] = 'Server error: ' . $e->getMessage();
    echo json_encode($response);
}

ob_end_flush();
?>
