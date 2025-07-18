-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 18, 2025 at 06:14 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `laundry_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `laundry_orders`
--

CREATE TABLE `laundry_orders` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  `weight_kg` decimal(5,2) NOT NULL,
  `time_slot` datetime NOT NULL,
  `notes` text DEFAULT NULL,
  `status` varchar(100) DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `laundry_orders`
--

INSERT INTO `laundry_orders` (`id`, `user_id`, `service_id`, `weight_kg`, `time_slot`, `notes`, `status`, `created_at`, `updated_at`) VALUES
(9, 5, 2, 4.00, '2025-07-18 09:30:00', '', 'On Delivery', '2025-07-18 13:05:17', '2025-07-18 13:22:22'),
(10, 7, 3, 3.00, '2025-07-18 10:30:00', '', 'On Delivery', '2025-07-18 14:18:43', '2025-07-18 14:21:22'),
(11, 8, 1, 0.50, '2025-07-18 08:00:00', '', 'pending', '2025-07-18 15:58:02', '2025-07-18 15:58:02');

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

CREATE TABLE `services` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `price_per_kg` decimal(6,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `services`
--

INSERT INTO `services` (`id`, `name`, `description`, `price_per_kg`) VALUES
(1, 'Wash, Dry & Iron', 'Wash, dry and iron the laundry', 20.00),
(2, 'Wash, Dry & Fold', 'Wash, dry and fold the laundry', 6.00),
(3, 'Wash, Dry & Stack', 'Wash, dry and stack the laundry', 4.50);

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `staffID` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`staffID`, `username`, `password`) VALUES
(3, 'Azlina', '$2y$10$ltEtlVXb6SygfuL/mvmsA.5GGcEbUz14LAJHBm0o.4rk75nBKVy2S');

-- --------------------------------------------------------

--
-- Table structure for table `status_change`
--

CREATE TABLE `status_change` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `staff_id` int(11) NOT NULL,
  `new_status` varchar(50) NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `status_change`
--

INSERT INTO `status_change` (`id`, `order_id`, `staff_id`, `new_status`, `changed_at`) VALUES
(1, 9, 3, '', '2025-07-18 13:15:48'),
(2, 9, 3, 'On Delivery', '2025-07-18 13:22:22'),
(3, 10, 3, 'In Progress', '2025-07-18 14:21:08'),
(4, 10, 3, 'On Delivery', '2025-07-18 14:21:22');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `phone_number`, `address`, `created_at`) VALUES
(1, 'ALYA SOFEA BINTI ABDUL GHAFAR', 'sopeahbalikakhirnya@gmail.com', '$2y$10$HLYuhFRFIDL9ZToeOFrvAu7clwrf4ufwHxrnnH.GBm1UQyJNE1tzm', '01145658798', 'NO 64, JALAN DI 4, TAMAN DESA IDAMAN, HANG TUAH JAYA, 76100 DURIAN TUNGGAL, MELAKA', '2025-07-15 16:29:39'),
(2, 'NUR HANIN AFRINA BINTI MOHAMMED SALLEH', 'haningaduhsql@gmail.com', '$2y$10$gbhK06c7.GxHld7Ro1twBuolDN3Jr2HnVTxSnijHMDWvQgf2a9uhu', '0163366788', 'NO 64, JALAN DI 4, TAMAN DESA IDAMAN, HANG TUAH JAYA, 76100 DURIAN TUNGGAL, MELAKA', '2025-07-15 16:33:51'),
(3, 'AZLINA BINTI MAT BAHRI', 'azlinadhgiveup@gmail.com', '$2y$10$rS1i4sEBGnLcvb.Sn2P/I.fnXUG87oCYuoP/sSfhLc6w1FF.UWiKu', '0124447475', 'NO 64, JALAN DI 4, TAMAN DESA IDAMAN, HANG TUAH JAYA', '2025-07-16 13:44:22'),
(4, 'AZLINA BINTI MAT BAHRI', 'letazlinasleep@gmail.com', '$2y$10$HGFWkiIE9hZ16pdlNcOTV.EMTfLLG1St/qFAP0p5QCrXrkdtYTGhS', '0132499897', 'NO.1, JLN BM 5/5, SEKSYEN 5, BANDAR BUKIT MAHKOTA, 43000, KAJANG, SELANGOR', '2025-07-17 15:41:56'),
(5, 'AZLINA BINTI MAT BAHRI', 'azlina@gmail.com', '$2y$10$Ck1jyk4e0KxPSfVFpYRWduj2mnfOtHvgu..AvdPmxAGQi8melzBQe', '0124447475', 'NO.1, JLN BM 5/5, SEKSYEN 5, BANDAR BUKIT MAHKOTA, 430000, KAJANG, SELANGOR', '2025-07-18 13:05:04'),
(7, 'NUR HIDAYAH AMANI BINTI SENIN', 'hidayahamani@gmail.com', '$2y$10$N.YvQcdrr5pa9RfFFj6OH.Y8Q0Dc8sAEcnTwsSBW4qqxYpM2XRlCW', '0162190554', 'LOT 2543 NO 24, JALAN KAMPUNG BANDAR DALAM, KAMPUNG BANDAR DALAM, WILAYAH PERSEKUTUAN KUALA LUMPUR, 51100', '2025-07-18 14:18:19'),
(8, 'NUR SAKEENAH BINTI SHAH KIRIN', 'sakeenahshah@gmail.com', '$2y$10$wz4psZHCxrgBmuwKG692BOK5vpwNsRy/KVf9TSA4rtSkZZ0e6UVNa', '0168787862', 'NO 60, JALAN DI 3, TAMAN DESA IDAMAN, HANG TUAH JAYA, 76100 DURIAN TUNGGAL, MELAKA', '2025-07-18 15:57:29');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `laundry_orders`
--
ALTER TABLE `laundry_orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `service_id` (`service_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`staffID`);

--
-- Indexes for table `status_change`
--
ALTER TABLE `status_change`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `staff_id` (`staff_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `laundry_orders`
--
ALTER TABLE `laundry_orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `services`
--
ALTER TABLE `services`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `staffID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `status_change`
--
ALTER TABLE `status_change`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `laundry_orders`
--
ALTER TABLE `laundry_orders`
  ADD CONSTRAINT `laundry_orders_ibfk_1` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `laundry_orders_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `status_change`
--
ALTER TABLE `status_change`
  ADD CONSTRAINT `fk_order` FOREIGN KEY (`order_id`) REFERENCES `laundry_orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `status_change_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `laundry_orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `status_change_ibfk_2` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staffID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
