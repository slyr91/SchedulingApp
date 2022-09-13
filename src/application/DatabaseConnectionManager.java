package application;

public class DatabaseConnectionManager {

    private static final String type = "mysql";
    private static final String host = "3.227.166.251";
    private static final String name = "U05Yun";
    private static final String username = "U05Yun";
    private static final String password = "53688642789";

    private static final String[] initializationString = {

            "--\n" +
            "-- Table structure for table `Address`\n" +
            "--\n" +
            "CREATE TABLE IF NOT EXISTS `Address` (\n" +
            " `addressId` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " `Address` varchar(50) NOT NULL,\n" +
            " `address2` varchar(50) NOT NULL,\n" +
            " `cityId` int(10) NOT NULL,\n" +
            " `postalCode` varchar(10) NOT NULL,\n" +
            " `phone` varchar(20) NOT NULL,\n" +
            " `createDate` datetime NOT NULL,\n" +
            " `createdBy` varchar(40) NOT NULL,\n" +
            " `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            " `lastUpdateBy` varchar(40) NOT NULL\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",

            "--\n" +
            "-- Table structure for table `appointment`\n" +
            "--\n" +
            "CREATE TABLE IF NOT EXISTS `appointment` (\n" +
            " `appointmentId` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " `customerId` int(10) NOT NULL,\n" +
            " `userId` int(11) NOT NULL,\n" +
            " `title` varchar(255) NOT NULL,\n" +
            " `description` text NOT NULL,\n" +
            " `location` text NOT NULL,\n" +
            " `contact` text NOT NULL,\n" +
            " `type` text NOT NULL,\n" +
            " `url` varchar(255) NOT NULL,\n" +
            " `start` datetime NOT NULL,\n" +
            " `end` datetime NOT NULL,\n" +
            " `createDate` datetime NOT NULL,\n" +
            " `createdBy` varchar(40) NOT NULL,\n" +
            " `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            " `lastUpdateBy` varchar(40) NOT NULL\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",

            "--\n" +
            "-- Table structure for table `city`\n" +
            "--\n" +
            "CREATE TABLE IF NOT EXISTS `city` (\n" +
            " `cityId` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " `city` varchar(50) NOT NULL,\n" +
            " `countryId` int(10) NOT NULL,\n" +
            " `createDate` datetime NOT NULL,\n" +
            " `createdBy` varchar(40) NOT NULL,\n" +
            " `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            " `lastUpdateBy` varchar(40) NOT NULL\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",

            "--\n" +
            "-- Table structure for table `country`\n" +
            "--\n" +
            "CREATE TABLE IF NOT EXISTS `country` (\n" +
            " `countryId` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " `country` varchar(50) NOT NULL,\n" +
            " `createDate` datetime NOT NULL,\n" +
            " `createdBy` varchar(40) NOT NULL,\n" +
            " `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            " `lastUpdateBy` varchar(40) NOT NULL\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",

            "--\n" +
            "-- Table structure for table `customer`\n" +
            "--\n" +
            "CREATE TABLE IF NOT EXISTS `customer` (\n" +
            " `customerId` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " `customerName` varchar(45) NOT NULL,\n" +
            " `addressId` int(10) NOT NULL,\n" +
            " `active` tinyint(1) NOT NULL,\n" +
            " `createDate` datetime NOT NULL,\n" +
            " `createdBy` varchar(40) NOT NULL,\n" +
            " `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            " `lastUpdateBy` varchar(40) NOT NULL\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",

            "--\n" +
            "-- Table structure for table `user`\n" +
            "--\n" +
            "CREATE TABLE IF NOT EXISTS `user` (\n" +
            " `userId` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " `userName` varchar(50) NOT NULL,\n" +
            " `password` varchar(50) NOT NULL,\n" +
            " `active` tinyint(4) NOT NULL,\n" +
            " `createDate` datetime NOT NULL,\n" +
            " `createdBy` varchar(40) NOT NULL,\n" +
            " `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            " `lastUpdateBy` varchar(40) NOT NULL\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n",

//            "--\n" +
//            "-- Indexes for dumped tables\n" +
//            "--\n" +
//            "--\n" +
//            "-- Indexes for table `Address`\n" +
//            "--\n" +
//            "ALTER TABLE `Address`\n" +
//            " ADD KEY `cityId` (`cityId`);\n",
//
//            "--\n" +
//            "-- Indexes for table `appointment`\n" +
//            "--\n" +
//            "ALTER TABLE `appointment`\n" +
//            " ADD PRIMARY KEY (`appointmentId`), ADD KEY `customerId` (`customerId`), ADD KEY `userId`\n" +
//            "(`userId`);\n",
//
//            "--\n" +
//            "-- Indexes for table `city`\n" +
//            "--\n" +
//            "ALTER TABLE `city`\n" +
//            " ADD PRIMARY KEY (`cityId`), ADD KEY `countryId` (`countryId`);\n",
//
//            "--\n" +
//            "-- Indexes for table `country`\n" +
//            "--\n" +
//            "ALTER TABLE `country`\n" +
//            " ADD PRIMARY KEY (`countryId`);\n",
//
//            "--\n" +
//            "-- Indexes for table `customer`\n" +
//            "--\n" +
//            "ALTER TABLE `customer`\n" +
//            " ADD PRIMARY KEY (`customerId`), ADD KEY `addressId` (`addressId`);\n",
//
//            "--\n" +
//            "-- Indexes for table `user`\n" +
//            "--\n" +
//            "ALTER TABLE `user`\n" +
//            " ADD PRIMARY KEY (`userId`);\n",

            "--\n" +
            "-- Constraints for dumped tables\n" +
            "--\n" +
            "--\n" +
            "-- Constraints for table `Address`\n" +
            "--\n" +
            "ALTER TABLE `Address`\n" +
            "ADD CONSTRAINT address_city_const FOREIGN KEY (`cityId`) REFERENCES `city` (`cityId`);\n",

            "--\n" +
            "-- Constraints for table `appointment`\n" +
            "--\n" +
            "ALTER TABLE `appointment`\n" +
            "ADD CONSTRAINT appointment_customer_const FOREIGN KEY (`customerId`) REFERENCES `customer`(`customerId`),\n" +
            "ADD CONSTRAINT appointment_user_const FOREIGN KEY (`userId`) REFERENCES `user` (`userId`);\n",

            "--\n" +
            "-- Constraints for table `city`\n" +
            "--\n" +
            "ALTER TABLE `city`\n" +
            "ADD CONSTRAINT city_country_const FOREIGN KEY (`countryId`) REFERENCES `country` (`countryId`);\n",

            "--\n" +
            "-- Constraints for table `customer`\n" +
            "--\n" +
            "ALTER TABLE `customer`\n" +
            "ADD CONSTRAINT customer_address_const FOREIGN KEY (`addressId`) REFERENCES `Address` (`addressId`);"};

    public static String getURL() {
        return "jdbc:" + type + "://" + host + "/" + name;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String[] getInitializationString() {
        return initializationString;
    }
}
