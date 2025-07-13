import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class CarRentalSystem {
    private static List<Car> cars = new ArrayList<>();
    private static List<Customer> customers = new ArrayList<>();
    private static List<Rental> rentals = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeSystem();
        while (true) {
            System.out.println("\n===== Car Rental System =====");
            System.out.println("1. Manage Cars");
            System.out.println("2. Manage Customers");
            System.out.println("3. Manage Rentals");
            System.out.println("4. Reports");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    manageCars();
                    break;
                case 2:
                    manageCustomers();
                    break;
                case 3:
                    manageRentals();
                    break;
                case 4:
                    generateReports();
                    break;
                case 5:
                    System.out.println("Exiting system. Goodbye!");
                    System.exit(0);
            }
        }
    }

    private static void initializeSystem() {
        cars.add(new Car("C001", "Toyota", "Innova", 2020, 2500.0, true));
        cars.add(new Car("C002", "Hyundai", "Creta", 2019, 2200.0, true));
        cars.add(new Car("C003", "Mahindra", "Thar", 2021, 3000.0, true));
        cars.add(new Car("C004", "Tata", "Nexon", 2022, 2000.0, true));
      
        customers.add(new Customer("CT001", "Amit Sahu", "amit@example.com", "8254321098"));
        customers.add(new Customer("CT002", "Rahul Sharma", "rahul@example.com", "9876543210"));
        customers.add(new Customer("CT003", "Raj Singh", "raj@example.com", "7654321098"));
        customers.add(new Customer("CT004", "Priya Patel", "priya@example.com", "8765432109"));
        customers.add(new Customer("CT005", "Anesu Verma", "anesu@example.com", "7554765092"));
    }

    private static void manageCars() {
        while (true) {
            System.out.println("\n===== Manage Cars =====");
            System.out.println("1. List all cars");
            System.out.println("2. Add new car");
            System.out.println("3. Update car");
            System.out.println("4. Delete car");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    listAllCars();
                    break;
                case 2:
                    addNewCar();
                    break;
                case 3:
                    updateCar();
                    break;
                case 4:
                    deleteCar();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void listAllCars() {
        System.out.println("\n===== All Cars =====");
        if (cars.isEmpty()) {
            System.out.println("No cars available.");
            return;
        }

        System.out.printf("%-8s %-10s %-10s %-6s %-12s %-6s%n", 
            "Car ID", "Make", "Model", "Year", "Price/Day", "Available");
        for (Car car : cars) {
            System.out.printf("%-8s %-10s %-10s %-6d ₹%-11.2f %-6s%n",
                car.getCarId(), car.getMake(), car.getModel(), car.getYear(),
                car.getPricePerDay(), car.isAvailable() ? "Yes" : "No");
        }
    }

    private static void addNewCar() {
        System.out.println("\n===== Add New Car =====");
        
        System.out.print("Enter Car ID: ");
        String carId = scanner.nextLine();
        
        if (findCarById(carId) != null) {
            System.out.println("Car with this ID already exists!");
            return;
        }
        
        System.out.print("Enter Make: ");
        String make = scanner.nextLine();
        
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        
        System.out.print("Enter Year: ");
        int year = getIntInput(1900, LocalDate.now().getYear() + 1);
        
        System.out.print("Enter Price per Day (₹): ");
        double price = getDoubleInput(0, 10000);
        
        cars.add(new Car(carId, make, model, year, price, true));
        System.out.println("Car added successfully!");
    }

    private static void updateCar() {
        System.out.println("\n===== Update Car =====");
        System.out.print("Enter Car ID to update: ");
        String carId = scanner.nextLine();
        
        Car car = findCarById(carId);
        if (car == null) {
            System.out.println("Car not found!");
            return;
        }
        
        System.out.println("Current car details:");
        System.out.println("1. Make: " + car.getMake());
        System.out.println("2. Model: " + car.getModel());
        System.out.println("3. Year: " + car.getYear());
        System.out.println("4. Price per Day: " + car.getPricePerDay());
        System.out.println("5. Available: " + (car.isAvailable() ? "Yes" : "No"));
        System.out.println("6. Cancel update");
        
        System.out.print("Enter the number of the field to update: ");
        int field = getIntInput(1, 6);
        
        if (field == 6) return;
        
        switch (field) {
            case 1:
                System.out.print("Enter new Make: ");
                car.setMake(scanner.nextLine());
                break;
            case 2:
                System.out.print("Enter new Model: ");
                car.setModel(scanner.nextLine());
                break;
            case 3:
                System.out.print("Enter new Year: ");
                car.setYear(getIntInput(1900, LocalDate.now().getYear() + 1));
                break;
            case 4:
                System.out.print("Enter new Price per Day (₹): ");
                car.setPricePerDay(getDoubleInput(0, 10000));
                break;
            case 5:
                System.out.print("Is car available? (yes/no): ");
                String availability = scanner.nextLine().toLowerCase();
                car.setAvailable(availability.equals("yes"));
                break;
        }
        
        System.out.println("Car updated successfully!");
    }

    private static void deleteCar() {
        System.out.println("\n===== Delete Car =====");
        System.out.print("Enter Car ID to delete: ");
        String carId = scanner.nextLine();
        
        Car car = findCarById(carId);
        if (car == null) {
            System.out.println("Car not found!");
            return;
        }
        
        if (!car.isAvailable()) {
            System.out.println("Cannot delete car that is currently rented!");
            return;
        }
        
        cars.remove(car);
        System.out.println("Car deleted successfully!");
    }

    private static void manageCustomers() {
        while (true) {
            System.out.println("\n===== Manage Customers =====");
            System.out.println("1. List all customers");
            System.out.println("2. Add new customer");
            System.out.println("3. Update customer");
            System.out.println("4. Delete customer");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    listAllCustomers();
                    break;
                case 2:
                    addNewCustomer();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void listAllCustomers() {
        System.out.println("\n===== All Customers =====");
        if (customers.isEmpty()) {
            System.out.println("No customers available.");
            return;
        }

        System.out.printf("%-8s %-20s %-25s %-12s%n", 
            "Cust ID", "Name", "Email", "Phone");
        for (Customer customer : customers) {
            System.out.printf("%-8s %-20s %-25s %-12s%n",
                customer.getCustomerId(), customer.getName(), 
                customer.getEmail(), customer.getPhone());
        }
    }

    private static void addNewCustomer() {
        System.out.println("\n===== Add New Customer =====");
        
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        
        if (findCustomerById(customerId) != null) {
            System.out.println("Customer with this ID already exists!");
            return;
        }
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        
        customers.add(new Customer(customerId, name, email, phone));
        System.out.println("Customer added successfully!");
    }

    private static void updateCustomer() {
        System.out.println("\n===== Update Customer =====");
        System.out.print("Enter Customer ID to update: ");
        String customerId = scanner.nextLine();
        
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }
        
        System.out.println("Current customer details:");
        System.out.println("1. Name: " + customer.getName());
        System.out.println("2. Email: " + customer.getEmail());
        System.out.println("3. Phone: " + customer.getPhone());
        System.out.println("4. Cancel update");
        
        System.out.print("Enter the number of the field to update: ");
        int field = getIntInput(1, 4);
        
        if (field == 4) return;
        
        switch (field) {
            case 1:
                System.out.print("Enter new Name: ");
                customer.setName(scanner.nextLine());
                break;
            case 2:
                System.out.print("Enter new Email: ");
                customer.setEmail(scanner.nextLine());
                break;
            case 3:
                System.out.print("Enter new Phone: ");
                customer.setPhone(scanner.nextLine());
                break;
        }
        
        System.out.println("Customer updated successfully!");
    }

    private static void deleteCustomer() {
        System.out.println("\n===== Delete Customer =====");
        System.out.print("Enter Customer ID to delete: ");
        String customerId = scanner.nextLine();
        
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }
        
        if (hasActiveRentals(customerId)) {
            System.out.println("Cannot delete customer with active rentals!");
            return;
        }
        
        customers.remove(customer);
        System.out.println("Customer deleted successfully!");
    }

    private static void manageRentals() {
        while (true) {
            System.out.println("\n===== Manage Rentals =====");
            System.out.println("1. Rent a car");
            System.out.println("2. Return a car");
            System.out.println("3. List all rentals");
            System.out.println("4. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 4);

            switch (choice) {
                case 1:
                    rentCar();
                    break;
                case 2:
                    returnCar();
                    break;
                case 3:
                    listAllRentals();
                    break;
                case 4:
                    return;
            }
        }
    }

    private static void rentCar() {
        System.out.println("\n===== Rent a Car =====");
        
        List<Car> availableCars = getAvailableCars();
        if (availableCars.isEmpty()) {
            System.out.println("No cars available for rent.");
            return;
        }
        
        System.out.printf("%-8s %-10s %-10s %-6s %-12s%n", 
            "Car ID", "Make", "Model", "Year", "Price/Day");
        for (Car car : availableCars) {
            System.out.printf("%-8s %-10s %-10s %-6d ₹%-11.2f%n",
                car.getCarId(), car.getMake(), car.getModel(), car.getYear(),
                car.getPricePerDay());
        }
        
        System.out.print("Enter Car ID to rent: ");
        String carId = scanner.nextLine();
        Car car = findCarById(carId);
        if (car == null || !car.isAvailable()) {
            System.out.println("Invalid car selection or car not available.");
            return;
        }
        
        System.out.println("\nCustomers:");
        listAllCustomers();
        
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }
        
        System.out.print("Enter rental start date (YYYY-MM-DD): ");
        LocalDate startDate = getDateInput();
        
        System.out.print("Enter rental end date (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput();
        
        if (endDate.isBefore(startDate)) {
            System.out.println("End date cannot be before start date!");
            return;
        }
        
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double totalCost = days * car.getPricePerDay();
        
        System.out.printf("\nRental Summary:%n");
        System.out.printf("Car: %s %s %d%n", car.getMake(), car.getModel(), car.getYear());
        System.out.printf("Customer: %s%n", customer.getName());
        System.out.printf("Period: %s to %s (%d days)%n", startDate, endDate, days);
        System.out.printf("Total Cost: ₹%.2f%n", totalCost);
        
        System.out.print("Confirm rental? (yes/no): ");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (confirm.equals("yes")) {
            String rentalId = "R" + System.currentTimeMillis();
            Rental rental = new Rental(rentalId, carId, customerId, startDate, endDate, totalCost, false);
            rentals.add(rental);
            car.setAvailable(false);
            System.out.println("Rental confirmed! Rental ID: " + rentalId);
        } else {
            System.out.println("Rental canceled.");
        }
    }

    private static void returnCar() {
        System.out.println("\n===== Return a Car =====");
        
        List<Rental> activeRentals = getActiveRentals();
        if (activeRentals.isEmpty()) {
            System.out.println("No active rentals found.");
            return;
        }
        
        System.out.println("Active Rentals:");
        System.out.printf("%-12s %-8s %-20s %-10s %-10s %-12s%n", 
            "Rental ID", "Car ID", "Customer", "Start Date", "End Date", "Cost");
        for (Rental rental : activeRentals) {
            Customer customer = findCustomerById(rental.getCustomerId());
            System.out.printf("%-12s %-8s %-20s %-10s %-10s ₹%-11.2f%n",
                rental.getRentalId(), rental.getCarId(), customer.getName(),
                rental.getStartDate(), rental.getEndDate(), rental.getTotalCost());
        }
        
        System.out.print("Enter Rental ID to return: ");
        String rentalId = scanner.nextLine();
        
        Rental rental = findRentalById(rentalId);
        if (rental == null || rental.isReturned()) {
            System.out.println("Invalid rental ID or already returned.");
            return;
        }
        
        LocalDate returnDate = LocalDate.now();
        if (returnDate.isAfter(rental.getEndDate())) {
            long lateDays = ChronoUnit.DAYS.between(rental.getEndDate(), returnDate);
            double lateFee = lateDays * 500.0; // ₹500 per day late fee
            System.out.printf("Car returned %d days late. Late fee: ₹%.2f%n", lateDays, lateFee);
            rental.setTotalCost(rental.getTotalCost() + lateFee);
        }
        
        rental.setReturned(true);
        Car car = findCarById(rental.getCarId());
        if (car != null) {
            car.setAvailable(true);
        }
        
        System.out.println("Car returned successfully!");
        System.out.printf("Total amount charged: ₹%.2f%n", rental.getTotalCost());
    }

    private static void listAllRentals() {
        System.out.println("\n===== All Rentals =====");
        if (rentals.isEmpty()) {
            System.out.println("No rentals found.");
            return;
        }

        System.out.printf("%-12s %-8s %-20s %-10s %-10s %-12s %-8s%n", 
            "Rental ID", "Car ID", "Customer", "Start Date", "End Date", "Cost", "Returned");
        for (Rental rental : rentals) {
            Customer customer = findCustomerById(rental.getCustomerId());
            System.out.printf("%-12s %-8s %-20s %-10s %-10s ₹%-11.2f %-8s%n",
                rental.getRentalId(), rental.getCarId(), customer.getName(),
                rental.getStartDate(), rental.getEndDate(), rental.getTotalCost(),
                rental.isReturned() ? "Yes" : "No");
        }
    }

    private static void generateReports() {
        while (true) {
            System.out.println("\n===== Reports =====");
            System.out.println("1. Current rentals");
            System.out.println("2. Rental history");
            System.out.println("3. Revenue report");
            System.out.println("4. Available cars");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    currentRentalsReport();
                    break;
                case 2:
                    rentalHistoryReport();
                    break;
                case 3:
                    revenueReport();
                    break;
                case 4:
                    availableCarsReport();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void currentRentalsReport() {
        System.out.println("\n===== Current Rentals Report =====");
        List<Rental> activeRentals = getActiveRentals();
        
        if (activeRentals.isEmpty()) {
            System.out.println("No current rentals.");
            return;
        }
        
        double totalRevenue = 0;
        System.out.printf("%-12s %-8s %-20s %-10s %-10s %-12s%n", 
            "Rental ID", "Car ID", "Customer", "Start Date", "End Date", "Cost");
        for (Rental rental : activeRentals) {
            Customer customer = findCustomerById(rental.getCustomerId());
            System.out.printf("%-12s %-8s %-20s %-10s %-10s ₹%-11.2f%n",
                rental.getRentalId(), rental.getCarId(), customer.getName(),
                rental.getStartDate(), rental.getEndDate(), rental.getTotalCost());
            totalRevenue += rental.getTotalCost();
        }
        
        System.out.printf("%nTotal expected revenue from current rentals: ₹%.2f%n", totalRevenue);
    }

    private static void rentalHistoryReport() {
        System.out.println("\n===== Rental History Report =====");
        if (rentals.isEmpty()) {
            System.out.println("No rental history.");
            return;
        }
        
        rentals.sort((r1, r2) -> r2.getStartDate().compareTo(r1.getStartDate()));
        
        double totalRevenue = 0;
        System.out.printf("%-12s %-8s %-20s %-10s %-10s %-12s %-8s%n", 
            "Rental ID", "Car ID", "Customer", "Start Date", "End Date", "Cost", "Returned");
        for (Rental rental : rentals) {
            Customer customer = findCustomerById(rental.getCustomerId());
            System.out.printf("%-12s %-8s %-20s %-10s %-10s ₹%-11.2f %-8s%n",
                rental.getRentalId(), rental.getCarId(), customer.getName(),
                rental.getStartDate(), rental.getEndDate(), rental.getTotalCost(),
                rental.isReturned() ? "Yes" : "No");
            if (rental.isReturned()) {
                totalRevenue += rental.getTotalCost();
            }
        }
        
        System.out.printf("%nTotal revenue from completed rentals: ₹%.2f%n", totalRevenue);
    }

    private static void revenueReport() {
        System.out.println("\n===== Revenue Report =====");
        
        double currentRevenue = 0;
        double completedRevenue = 0;
        
        for (Rental rental : rentals) {
            if (rental.isReturned()) {
                completedRevenue += rental.getTotalCost();
            } else {
                currentRevenue += rental.getTotalCost();
            }
        }
        
        System.out.printf("Expected revenue from current rentals: ₹%.2f%n", currentRevenue);
        System.out.printf("Revenue from completed rentals: ₹%.2f%n", completedRevenue);
        System.out.printf("Total revenue (current + completed): ₹%.2f%n", currentRevenue + completedRevenue);
    }

    private static void availableCarsReport() {
        System.out.println("\n===== Available Cars Report =====");
        List<Car> availableCars = getAvailableCars();
        
        if (availableCars.isEmpty()) {
            System.out.println("No cars currently available.");
            return;
        }
        
        System.out.printf("%-8s %-10s %-10s %-6s %-12s%n", 
            "Car ID", "Make", "Model", "Year", "Price/Day");
        for (Car car : availableCars) {
            System.out.printf("%-8s %-10s %-10s %-6d ₹%-11.2f%n",
                car.getCarId(), car.getMake(), car.getModel(), 
                car.getYear(), car.getPricePerDay());
        }
    }

    // Helper methods
    private static Car findCarById(String carId) {
        for (Car car : cars) {
            if (car.getCarId().equalsIgnoreCase(carId)) {
                return car;
            }
        }
        return null;
    }

    private static Customer findCustomerById(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        return null;
    }

    private static Rental findRentalById(String rentalId) {
        for (Rental rental : rentals) {
            if (rental.getRentalId().equalsIgnoreCase(rentalId)) {
                return rental;
            }
        }
        return null;
    }

    private static boolean hasActiveRentals(String customerId) {
        for (Rental rental : rentals) {
            if (rental.getCustomerId().equalsIgnoreCase(customerId) && !rental.isReturned()) {
                return true;
            }
        }
        return false;
    }

    private static List<Car> getAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.isAvailable()) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    private static List<Rental> getActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (!rental.isReturned()) {
                activeRentals.add(rental);
            }
        }
        return activeRentals;
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static double getDoubleInput(double min, double max) {
        while (true) {
            try {
                double input = Double.parseDouble(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %.2f and %.2f: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static LocalDate getDateInput() {
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.print("Invalid date format. Please use YYYY-MM-DD: ");
            }
        }
    }

    // Inner classes for data models
    static class Car {
        private String carId;
        private String make;
        private String model;
        private int year;
        private double pricePerDay;
        private boolean available;

        public Car(String carId, String make, String model, int year, double pricePerDay, boolean available) {
            this.carId = carId;
            this.make = make;
            this.model = model;
            this.year = year;
            this.pricePerDay = pricePerDay;
            this.available = available;
        }

        public String getCarId() { return carId; }
        public String getMake() { return make; }
        public void setMake(String make) { this.make = make; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public double getPricePerDay() { return pricePerDay; }
        public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }

    static class Customer {
        private String customerId;
        private String name;
        private String email;
        private String phone;

        public Customer(String customerId, String name, String email, String phone) {
            this.customerId = customerId;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        public String getCustomerId() { return customerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    static class Rental {
        private String rentalId;
        private String carId;
        private String customerId;
        private LocalDate startDate;
        private LocalDate endDate;
        private double totalCost;
        private boolean returned;

        public Rental(String rentalId, String carId, String customerId, LocalDate startDate, 
                     LocalDate endDate, double totalCost, boolean returned) {
            this.rentalId = rentalId;
            this.carId = carId;
            this.customerId = customerId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalCost = totalCost;
            this.returned = returned;
        }

        public String getRentalId() { return rentalId; }
        public String getCarId() { return carId; }
        public String getCustomerId() { return customerId; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public double getTotalCost() { return totalCost; }
        public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
        public boolean isReturned() { return returned; }
        public void setReturned(boolean returned) { this.returned = returned; }
    }
}
