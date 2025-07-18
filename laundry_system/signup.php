<?php

header("Content-Type: application/json");

include("db.php");



$name = $_POST['name'] ?? '';

$email = $_POST['email'] ?? '';

$password = $_POST['password'] ?? '';

$phone = $_POST['phone'] ?? '';

$address = $_POST['address'] ?? '';



$response = [];



if ($name && $email && $password && $phone && $address) {

    // ✅ Hash the password before storing

    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);



    $stmt = $conn->prepare("INSERT INTO users (name, email, password, phone_number, address) VALUES (?, ?, ?, ?, ?)");

    $stmt->bind_param("sssss", $name, $email, $hashedPassword, $phone, $address);



    if ($stmt->execute()) {

        $user_id = $conn->insert_id;

        $response = [

            "status" => "success",

            "message" => "User registered successfully.",

            "user_id" => $user_id

        ];

    } else {

        $response = [

            "status" => "error",

            "message" => "Database insert failed.",

            "error" => $conn->error

        ];

    }



    $stmt->close();

} else {

    $response = [

        "status" => "error",

        "message" => "Missing required fields."

    ];

}



$conn->close();

echo json_encode($response);

?>