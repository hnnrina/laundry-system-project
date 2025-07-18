<?php
require_once __DIR__ . '/vendor/autoload.php';
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$jwt_secret = 'powerupGo'; 

function generateJWT($userId, $role = 'customer') {
    global $jwt_secret;
    $payload = [
        'iss' => 'laundry_system',          // issuer
        'aud' => 'laundry_clients',         // audience
        'iat' => time(),                    // issued at
        'exp' => time() + 3600,             // expire in 1 hour
        'uid' => $userId,
        'role' => $role
    ];
    return JWT::encode($payload, $jwt_secret, 'HS256');
}

function validateJWT($token) {
    global $jwt_secret;
    try {
        $decoded = JWT::decode($token, new Key($jwt_secret, 'HS256'));
        return (array)$decoded;
    } catch (Exception $e) {
        return null;
    }
}
?>
