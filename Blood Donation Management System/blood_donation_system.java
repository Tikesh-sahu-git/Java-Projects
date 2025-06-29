import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class User {
    String username;
    String password;
    String role; // "donor", "hospital", "admin"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

class Donor {
    String name;
    String username;
    String bloodGroup;
    int age;
    String contact;
    String location;
    String lastDonationDate;
    boolean eligible;
    List<String> donationHistory;

    public Donor(String name, String username, String bloodGroup, int age,
                 String contact, String location) {
        this.name = name;
        this.username = username;
        this.bloodGroup = bloodGroup;
        this.age = age;
        this.contact = contact;
        this.location = location;
        this.lastDonationDate = "Never";
        this.donationHistory = new ArrayList<>();
        this.eligible = checkEligibility();
    }

    private boolean checkEligibility() {
        return age >= 18 && age <= 65;
    }

    public void updateDonation() {
        this.lastDonationDate = LocalDate.now().format(BloodDonationSystem.dateFormatter);
        this.donationHistory.add(this.lastDonationDate);
    }

    public boolean canDonateAgain() {
        if (lastDonationDate.equals("Never")) {
            return true;
        }
        try {
            LocalDate lastDate = LocalDate.parse(lastDonationDate, BloodDonationSystem.dateFormatter);
            long daysBetween = ChronoUnit.DAYS.between(lastDate, LocalDate.now());
            return daysBetween >= 56;
        } catch (Exception e) {
            System.err.println("Error calculating donation eligibility: " + e.getMessage());
            return false;
        }
    }
}

class Hospital {
    String name;
    String username;
    String location;
    String contact;

    public Hospital(String name, String username, String location, String contact) {
        this.name = name;
        this.username = username;
        this.location = location;
        this.contact = contact;
    }
}

class BloodRequest {
    String patientName;
    String hospitalUsername;
    String hospitalName;
    String bloodGroup;
    int units;
    String requestDate;
    String location;
    String urgency;
    boolean fulfilled;

    public BloodRequest(String patientName, String hospitalUsername, String hospitalName, String bloodGroup,
                        int units, String location, String urgency) {
        this.patientName = patientName;
        this.hospitalUsername = hospitalUsername;
        this.hospitalName = hospitalName;
        this.bloodGroup = bloodGroup;
        this.units = units;
        this.requestDate = LocalDate.now().format(BloodDonationSystem.dateFormatter);
        this.location = location;
        this.urgency = urgency;
        this.fulfilled = false;
    }
}

public class BloodDonationSystem {
    private static HashMap<String, User> users = new HashMap<>();
    private static HashMap<String, Donor> donors = new HashMap<>();
    private static HashMap<String, Hospital> hospitals = new HashMap<>();
    private static ArrayList<BloodRequest> bloodRequests = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void main(String[] args) {
        initializeSampleData();

        System.out.println("🩸 BLOOD DONATION MANAGEMENT SYSTEM 🩸");
        System.out.println("--------------------------------------");

        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void initializeSampleData() {
        // Admin user
        users.put("admin", new User("admin", "admin123", "admin"));

        // Sample Donors
        users.put("john_d", new User("john_d", "donorpass", "donor"));
        donors.put("john_d", new Donor("John Doe", "john_d", "O+", 28, "555-111-2222", "Downtown"));

        users.put("old_d", new User("old_d", "oldpass", "donor"));
        donors.put("old_d", new Donor("Elderly Donor", "old_d", "A-", 70, "555-333-4444", "Uptown"));

        users.put("recent_d", new User("recent_d", "recentpass", "donor"));
        Donor recentDonor = new Donor("Recent Donor", "recent_d", "B+", 35, "555-555-6666", "Midtown");
        recentDonor.lastDonationDate = LocalDate.now().minusDays(30).format(dateFormatter);
        recentDonor.donationHistory.add(recentDonor.lastDonationDate);
        donors.put("recent_d", recentDonor);

        // Sample Hospitals
        users.put("cityhosp", new User("cityhosp", "hosp123", "hospital"));
        hospitals.put("cityhosp", new Hospital("City General Hospital", "cityhosp", "Downtown", "555-987-6543"));

        users.put("northhosp", new User("northhosp", "nhosp456", "hospital"));
        hospitals.put("northhosp", new Hospital("Northside Clinic", "northhosp", "Uptown", "555-123-4567"));

        // Sample Blood Requests
        Hospital cityHospital = hospitals.get("cityhosp");
        bloodRequests.add(new BloodRequest("Patient Alpha", cityHospital.username, cityHospital.name, "O+", 3, cityHospital.location, "urgent"));
        bloodRequests.add(new BloodRequest("Patient Beta", cityHospital.username, cityHospital.name, "AB-", 1, cityHospital.location, "normal"));

        Hospital northHospital = hospitals.get("northhosp");
        bloodRequests.add(new BloodRequest("Patient Gamma", northHospital.username, northHospital.name, "A-", 2, northHospital.location, "critical"));
    }

    private static void showLoginMenu() {
        System.out.println("\n🔐 LOGIN MENU");
        System.out.println("1. Login");
        System.out.println("2. Register as Donor");
        System.out.println("3. Register as Hospital");
        System.out.println("4. Exit");
        System.out.print("Choose an option (1-4): ");

        int choice = getIntInput(1, 4);
        scanner.nextLine();

        switch (choice) {
            case 1: login(); break;
            case 2: registerDonor(); break;
            case 3: registerHospital(); break;
            case 4: exitSystem();
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    private static void login() {
        System.out.println("\n🔑 LOGIN");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.password.equals(password)) {
            currentUser = user;
            System.out.println("\n✅ Login successful! Welcome, " + username + "!");
        } else {
            System.out.println("❌ Invalid username or password.");
        }
    }

    private static void registerDonor() {
        System.out.println("\n🆕 DONOR REGISTRATION");
        
        String username = getUniqueUsername();
        String password = getValidPassword();
        
        System.out.print("Full name: ");
        String name = scanner.nextLine();
        
        String bloodGroup = getValidBloodGroup();
        
        System.out.print("Age: ");
        int age = getIntInput(1, 120);
        scanner.nextLine();
        
        System.out.print("Contact number: ");
        String contact = scanner.nextLine();

        System.out.print("Your location (e.g., Downtown, Uptown, Midtown): ");
        String location = scanner.nextLine();

        users.put(username, new User(username, password, "donor"));
        donors.put(username, new Donor(name, username, bloodGroup, age, contact, location));
        
        System.out.println("\n✅ Donor registration successful!");
    }

    private static void registerHospital() {
        System.out.println("\n🏥 HOSPITAL REGISTRATION");
        
        String username = getUniqueUsername();
        String password = getValidPassword();
        
        System.out.print("Hospital name: ");
        String name = scanner.nextLine();
        
        System.out.print("Location (e.g., Downtown, Uptown, Midtown): ");
        String location = scanner.nextLine();
        
        System.out.print("Contact number: ");
        String contact = scanner.nextLine();

        users.put(username, new User(username, password, "hospital"));
        hospitals.put(username, new Hospital(name, username, location, contact));
        
        System.out.println("\n✅ Hospital registration successful!");
    }

    private static void showMainMenu() {
        boolean exitRoleMenu = false;
        while (!exitRoleMenu) {
            if (currentUser.role.equals("donor")) {
                exitRoleMenu = showDonorMenu();
            } else if (currentUser.role.equals("hospital")) {
                exitRoleMenu = showHospitalMenu();
            } else if (currentUser.role.equals("admin")) {
                exitRoleMenu = showAdminMenu();
            }
        }
    }

    private static boolean showDonorMenu() {
        System.out.println("\n🧑 DONOR MENU");
        System.out.println("1. View My Profile");
        System.out.println("2. Record Blood Donation");
        System.out.println("3. View Blood Requests");
        System.out.println("4. Check Donation Eligibility");
        System.out.println("5. Logout");
        System.out.print("Choose an option (1-5): ");

        int choice = getIntInput(1, 5);
        scanner.nextLine();

        switch (choice) {
            case 1: viewDonorProfile(); break;
            case 2: recordDonation(); break;
            case 3: viewMatchingBloodRequestsForDonor(); break;
            case 4: checkDonationEligibilityForDonor(); break;
            case 5: logout(); return true;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
        return false;
    }

    private static boolean showHospitalMenu() {
        System.out.println("\n🏥 HOSPITAL MENU");
        System.out.println("1. View Hospital Profile");
        System.out.println("2. Create Blood Request");
        System.out.println("3. View My Blood Requests");
        System.out.println("4. Find Donors");
        System.out.println("5. Logout");
        System.out.print("Choose an option (1-5): ");

        int choice = getIntInput(1, 5);
        scanner.nextLine();

        switch (choice) {
            case 1: viewHospitalProfile(); break;
            case 2: createRequest(); break;
            case 3: viewHospitalRequests(); break;
            case 4: findDonors(); break;
            case 5: logout(); return true;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
        return false;
    }

    private static boolean showAdminMenu() {
        System.out.println("\n👑 ADMIN MENU");
        System.out.println("1. View All Donors");
        System.out.println("2. View All Blood Requests");
        System.out.println("3. Send Donation Reminders");
        System.out.println("4. View System Analytics");
        System.out.println("5. Logout");
        System.out.print("Choose an option (1-5): ");

        int choice = getIntInput(1, 5);
        scanner.nextLine();

        switch (choice) {
            case 1: viewAllDonors(); break;
            case 2: viewAllBloodRequestsForAdmin(); break;
            case 3: sendDonationReminders(); break;
            case 4: viewSystemAnalytics(); break;
            case 5: logout(); return true;
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
        return false;
    }

    private static void viewDonorProfile() {
        Donor donor = donors.get(currentUser.username);
        if (donor == null) {
            System.out.println("❌ Your donor profile is not complete.");
            return;
        }
        System.out.println("\n🧑 DONOR PROFILE");
        System.out.println("Name: " + donor.name);
        System.out.println("Blood Group: " + donor.bloodGroup);
        System.out.println("Age: " + donor.age);
        System.out.println("Contact: " + donor.contact);
        System.out.println("Location: " + donor.location);
        System.out.println("Last Donation: " + donor.lastDonationDate);
        System.out.println("Total Donations: " + donor.donationHistory.size());
        System.out.println("Eligibility (Age 18-65): " + (donor.eligible ? "✅ Eligible" : "❌ Not Eligible"));
        System.out.println("Can Donate Again (56-day rule): " + (donor.canDonateAgain() ? "✅ Yes" : "❌ No"));
    }

    private static void recordDonation() {
        Donor donor = donors.get(currentUser.username);
        
        if (donor == null) {
            System.out.println("❌ Please complete your donor profile first.");
            return;
        }

        if (!donor.eligible) {
            System.out.println("❌ You are not eligible to donate blood due to age (must be 18-65).");
            return;
        }

        if (!donor.canDonateAgain()) {
            System.out.println("❌ You can only donate blood every 56 days. Last donation: " + donor.lastDonationDate);
            return;
        }
        
        System.out.print("\nEnter donation center name: ");
        String center = scanner.nextLine();

        donor.updateDonation();
        System.out.println("\n✅ Donation recorded successfully at " + center + "!");
        System.out.println("Last donation date: " + donor.lastDonationDate);
        System.out.println("Total donations: " + donor.donationHistory.size());

        notifyHospitalsOfDonation(donor);
    }

    private static void notifyHospitalsOfDonation(Donor donor) {
        System.out.println("🔔 Checking for urgent requests to notify...");
        int hospitalsNotified = 0;
        for (BloodRequest request : bloodRequests) {
            if (!request.fulfilled && request.bloodGroup.equalsIgnoreCase(donor.bloodGroup) &&
                request.location.equalsIgnoreCase(donor.location) &&
                (request.urgency.equals("urgent") || request.urgency.equals("critical"))) {

                Hospital hospital = hospitals.get(request.hospitalUsername);
                if (hospital != null) {
                    sendSMS(hospital.contact, 
                            "🩸 New donor (" + donor.name + ", " + donor.bloodGroup + 
                            ") in your area has just donated! May help fulfill request for " + request.patientName +
                            ". Contact: " + donor.contact);
                    hospitalsNotified++;
                }
            }
        }
        if (hospitalsNotified > 0) {
            System.out.println("✅ Notified " + hospitalsNotified + " hospitals about your donation.");
        } else {
            System.out.println("No urgent hospital requests matched your donation yet.");
        }
    }

    private static void createRequest() {
        Hospital hospital = hospitals.get(currentUser.username);
        if (hospital == null) {
            System.out.println("❌ Your hospital profile is not complete.");
            return;
        }

        System.out.println("\n🆘 CREATE BLOOD REQUEST");
        
        System.out.print("Patient name: ");
        String patientName = scanner.nextLine();

        String bloodGroup = getValidBloodGroup();
        
        System.out.print("Units needed: ");
        int units = getIntInput(1, 100);
        scanner.nextLine();
        
        String urgency = "";
        while (!urgency.equalsIgnoreCase("normal") && !urgency.equalsIgnoreCase("urgent") && !urgency.equalsIgnoreCase("critical")) {
            System.out.print("Urgency level (normal/urgent/critical): ");
            urgency = scanner.nextLine().toLowerCase();
            if (!urgency.equalsIgnoreCase("normal") && !urgency.equalsIgnoreCase("urgent") && !urgency.equalsIgnoreCase("critical")) {
                System.out.println("⚠️ Invalid urgency level. Please choose from normal, urgent, or critical.");
            }
        }

        BloodRequest newRequest = new BloodRequest(
            patientName, hospital.username, hospital.name, bloodGroup, units, hospital.location, urgency);
        bloodRequests.add(newRequest);
        System.out.println("\n✅ Blood request created successfully!");

        notifyMatchingDonorsForRequest(newRequest);
    }

    private static void notifyMatchingDonorsForRequest(BloodRequest request) {
        int donorsNotified = 0;
        for (Donor donor : donors.values()) {
            if (donor.bloodGroup.equalsIgnoreCase(request.bloodGroup) &&
                donor.location.equalsIgnoreCase(request.location) &&
                donor.eligible && donor.canDonateAgain()) {
                
                sendSMS(donor.contact, 
                        "🚨 Urgent Blood Needed! Type: " + request.bloodGroup + 
                        ", Units: " + request.units + " at " + request.hospitalName + 
                        " (" + request.location + "). Urgency: " + request.urgency.toUpperCase() + 
                        ". Patient: " + request.patientName + ". Please consider donating. " + request.hospitalName + " contact: " + hospitals.get(request.hospitalUsername).contact);
                donorsNotified++;
            }
        }
        System.out.println("🔔 Notified " + donorsNotified + " eligible donors about this request.");
    }

    private static void viewMatchingBloodRequestsForDonor() {
        Donor currentDonor = donors.get(currentUser.username);
        if (currentDonor == null) {
            System.out.println("❌ Please complete your donor profile first.");
            return;
        }

        System.out.println("\n🆘 BLOOD REQUESTS MATCHING YOUR PROFILE (" + currentDonor.bloodGroup + " in " + currentDonor.location + ")");
        boolean found = false;

        for (BloodRequest request : bloodRequests) {
            if (!request.fulfilled && 
                request.bloodGroup.equalsIgnoreCase(currentDonor.bloodGroup) &&
                request.location.equalsIgnoreCase(currentDonor.location)) {
                
                System.out.println("\nPatient: " + request.patientName);
                System.out.println("Hospital: " + request.hospitalName);
                System.out.println("Blood Group Needed: " + request.bloodGroup);
                System.out.println("Units Required: " + request.units);
                System.out.println("Request Date: " + request.requestDate);
                System.out.println("Urgency: " + request.urgency);
                System.out.println("Hospital Contact: " + hospitals.get(request.hospitalUsername).contact);
                System.out.println("----------------------");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("✅ No active blood requests matching your blood type and location.");
        }
    }

    private static void viewHospitalRequests() {
        Hospital currentHospital = hospitals.get(currentUser.username);
        if (currentHospital == null) {
            System.out.println("❌ Your hospital profile is not complete.");
            return;
        }

        System.out.println("\n🏥 YOUR HOSPITAL'S BLOOD REQUESTS (" + currentHospital.name + ")");
        boolean found = false;
        for (BloodRequest request : bloodRequests) {
            if (request.hospitalUsername.equals(currentUser.username)) {
                System.out.println("\nPatient: " + request.patientName);
                System.out.println("Blood Group Needed: " + request.bloodGroup);
                System.out.println("Units Required: " + request.units);
                System.out.println("Request Date: " + request.requestDate);
                System.out.println("Location: " + request.location);
                System.out.println("Urgency: " + request.urgency);
                System.out.println("Status: " + (request.fulfilled ? "✅ Fulfilled" : "🆘 Active"));
                System.out.println("----------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("✅ No blood requests made by your hospital yet.");
        }
    }

    private static void viewHospitalProfile() {
        Hospital hospital = hospitals.get(currentUser.username);
        if (hospital == null) {
            System.out.println("❌ Your hospital profile is not complete.");
            return;
        }
        System.out.println("\n🏥 HOSPITAL PROFILE");
        System.out.println("Name: " + hospital.name);
        System.out.println("Username: " + hospital.username);
        System.out.println("Location: " + hospital.location);
        System.out.println("Contact: " + hospital.contact);
    }

    private static void findDonors() {
        System.out.println("\n🔍 FIND DONORS");
        System.out.print("Enter required blood group: ");
        String bloodGroup = getValidBloodGroup();
        
        System.out.print("Enter location to search (leave blank for all): ");
        String locationFilter = scanner.nextLine();

        System.out.println("\n🧑‍🤝‍🧑 Matching Donors:");
        boolean found = false;
        
        for (Donor donor : donors.values()) {
            if (donor.bloodGroup.equalsIgnoreCase(bloodGroup) &&
                (locationFilter.isEmpty() || donor.location.equalsIgnoreCase(locationFilter)) &&
                donor.eligible && donor.canDonateAgain()) {
                
                System.out.println("Name: " + donor.name);
                System.out.println("Contact: " + donor.contact);
                System.out.println("Location: " + donor.location);
                System.out.println("Last Donation: " + donor.lastDonationDate);
                System.out.println("Total Donations: " + donor.donationHistory.size());
                System.out.println("----------------------");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("❌ No eligible donors found with blood group " + bloodGroup + 
                                (locationFilter.isEmpty() ? "" : " in " + locationFilter) + " who can donate now.");
        }
    }

    private static void checkDonationEligibilityForDonor() {
        Donor donor = donors.get(currentUser.username);
        if (donor == null) {
            System.out.println("❌ Please complete your donor profile first.");
            return;
        }
        
        System.out.println("\n✅ ELIGIBILITY CHECK");
        System.out.println("Age Check (18-65): " + (donor.eligible ? "✅ Passed" : "❌ Failed"));
        System.out.println("56-Day Rule Check: " + (donor.canDonateAgain() ? "✅ Passed" : "❌ Failed (last donation: " + donor.lastDonationDate + ")"));
        
        if (donor.eligible && donor.canDonateAgain()) {
            System.out.println("\n❤️ You are eligible to donate blood today!");
        } else {
            System.out.println("\n⚠️ You are not currently eligible to donate blood.");
        }
    }

    private static void viewAllDonors() {
        System.out.println("\n🧑‍🤝‍🧑 ALL REGISTERED DONORS (" + donors.size() + ")");
        if (donors.isEmpty()) {
            System.out.println("No donors registered yet.");
            return;
        }
        for (Donor donor : donors.values()) {
            System.out.println("\nUsername: " + donor.username);
            System.out.println("Name: " + donor.name);
            System.out.println("Blood Group: " + donor.bloodGroup);
            System.out.println("Age: " + donor.age);
            System.out.println("Contact: " + donor.contact);
            System.out.println("Location: " + donor.location);
            System.out.println("Last Donation: " + donor.lastDonationDate);
            System.out.println("Eligible (Age 18-65): " + (donor.eligible ? "✅ Yes" : "❌ No"));
            System.out.println("Can Donate Now (56-day rule): " + (donor.canDonateAgain() ? "✅ Yes" : "❌ No"));
            System.out.println("Total Donations: " + donor.donationHistory.size());
            System.out.println("----------------------");
        }
    }

    private static void viewAllBloodRequestsForAdmin() {
        System.out.println("\n🆘 ALL BLOOD REQUESTS (" + bloodRequests.size() + ")");
        if (bloodRequests.isEmpty()) {
            System.out.println("No blood requests active yet.");
            return;
        }
        for (BloodRequest request : bloodRequests) {
            System.out.println("\nPatient: " + request.patientName);
            System.out.println("Hospital: " + request.hospitalName + " (User: " + request.hospitalUsername + ")");
            System.out.println("Blood Group Needed: " + request.bloodGroup);
            System.out.println("Units Required: " + request.units);
            System.out.println("Request Date: " + request.requestDate);
            System.out.println("Location: " + request.location);
            System.out.println("Urgency: " + request.urgency);
            System.out.println("Status: " + (request.fulfilled ? "✅ Fulfilled" : "🆘 Active"));
            System.out.println("----------------------");
        }
    }

    private static void sendDonationReminders() {
        System.out.println("\n⏰ SENDING DONATION REMINDERS");
        int count = 0;
        for (Donor donor : donors.values()) {
            if (donor.eligible && donor.canDonateAgain()) {
                sendSMS(donor.contact, 
                        "Hello " + donor.name + "! You are eligible to donate blood again. " +
                        "Your contribution can save lives! Please visit a donation center. ❤️");
                count++;
            }
        }
        System.out.println("\n✅ Sent reminders to " + count + " eligible donors.");
    }

    private static void viewSystemAnalytics() {
        System.out.println("\n📈 SYSTEM ANALYTICS DASHBOARD");

        System.out.println("\n👥 User Statistics:");
        System.out.println("Total Donors: " + donors.size());
        System.out.println("Total Hospitals: " + hospitals.size());
        System.out.println("Total Blood Requests: " + bloodRequests.size());
        
        long fulfilledRequests = bloodRequests.stream().filter(r -> r.fulfilled).count();
        System.out.println("Fulfilled Requests: " + fulfilledRequests);
        System.out.println("Active Requests: " + (bloodRequests.size() - fulfilledRequests));

        // Blood group distribution
        Map<String, Integer> donorCountByGroup = new HashMap<>();
        for (Donor donor : donors.values()) {
            donorCountByGroup.put(donor.bloodGroup, donorCountByGroup.getOrDefault(donor.bloodGroup, 0) + 1);
        }
        System.out.println("\n🩸 Blood Group Distribution (Donors):");
        donorCountByGroup.forEach((group, count) -> 
            System.out.printf("%s: %d (%.1f%%)\n", 
                group, count, donors.isEmpty() ? 0.0 : (double)count/donors.size()*100));

        // Donation frequency
        long totalDonationsRecorded = donors.values().stream()
            .mapToInt(d -> d.donationHistory.size())
            .sum();
        System.out.println("\n⏱️ Donation Frequency:");
        System.out.println("Total Donations Recorded: " + totalDonationsRecorded);
        System.out.printf("Average Donations per Donor: %.1f\n", 
            donors.isEmpty() ? 0.0 : (double)totalDonationsRecorded / donors.size());
        
        // Urgency statistics
        Map<String, Integer> urgencyCount = new HashMap<>();
        for (BloodRequest request : bloodRequests) {
            urgencyCount.put(request.urgency, 
                urgencyCount.getOrDefault(request.urgency, 0) + 1);
        }
        System.out.println("\n🚨 Request Urgency Levels:");
        urgencyCount.forEach((level, count) -> 
            System.out.printf("%s: %d (%.1f%%)\n", 
                level, count, bloodRequests.isEmpty() ? 0.0 : (double)count/bloodRequests.size()*100));
    }

    private static void logout() {
        System.out.println("\n👋 Logging out " + currentUser.username + "...");
        currentUser = null;
    }

    private static void exitSystem() {
        System.out.println("\n❤️ Thank you for using the Blood Donation System!");
        scanner.close();
        System.exit(0);
    }

    // Helper Methods
    private static String getUniqueUsername() {
        String username;
        while (true) {
            System.out.print("Choose a username: ");
            username = scanner.nextLine();
            if (!users.containsKey(username)) break;
            System.out.println("❌ Username already exists. Please choose another.");
        }
        return username;
    }

    private static String getValidPassword() {
        String password;
        while (true) {
            System.out.print("Choose a password (min 6 characters): ");
            password = scanner.nextLine();
            if (password.length() >= 6) break;
            System.out.println("❌ Password too short. Must be at least 6 characters.");
        }
        return password;
    }

    private static String getValidBloodGroup() {
        String bloodGroup;
        while (true) {
            System.out.print("Blood group (A+/A-/B+/B-/AB+/AB-/O+/O-): ");
            bloodGroup = scanner.nextLine().toUpperCase();
            if (bloodGroup.matches("(A|B|AB|O)[+-]")) break;
            System.out.println("❌ Invalid blood group format! Example: O+, A-");
        }
        return bloodGroup;
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.printf("⚠️ Please enter a number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.print("⚠️ Invalid input. Please enter a whole number: ");
                scanner.next();
            }
        }
    }

    private static void sendSMS(String number, String message) {
        System.out.println("\n📱 Sending SMS to " + number + ": \"" + message + "\"");
    }
}
