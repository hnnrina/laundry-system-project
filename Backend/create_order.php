

<?php
include("db.php");

$data = json_decode(file_get_contents("php://input"));

$user_id = $data->user_id;
$service_id = $data->service_id;
$weight_kg = $data->weight_kg;
$time_slot = $data->time_slot;
$notes = $data->notes;

$stmt = $conn->prepare("INSERT INTO laundry_orders (user_id, service_id, weight_kg, time_slot, notes) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("iidss", $user_id, $service_id, $weight_kg, $time_slot, $notes);

$response = [];

if ($stmt->execute()) {
    $response["success"] = true;
} else {
    $response["success"] = false;
    $response["error"] = $conn->error;
}

echo json_encode($response);
?>