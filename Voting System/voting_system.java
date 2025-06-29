import java.util.*;
import java.io.*;

class Voter implements Serializable {
    private String voterId;
    private String name;
    private boolean hasVoted;

    public Voter(String voterId, String name) {
        this.voterId = voterId;
        this.name = name;
        this.hasVoted = false;
    }

    public String getVoterId() { return voterId; }
    public String getName() { return name; }
    public boolean hasVoted() { return hasVoted; }
    public void setVoted() { hasVoted = true; }
}

class Candidate implements Serializable {
    private String candidateId;
    private String name;
    private String party;
    private int votes;

    public Candidate(String candidateId, String name, String party) {
        this.candidateId = candidateId;
        this.name = name;
        this.party = party;
        this.votes = 0;
    }

    public String getCandidateId() { return candidateId; }
    public String getName() { return name; }
    public String getParty() { return party; }
    public int getVotes() { return votes; }
    public void incrementVotes() { votes++; }
}

public class VotingSystem {
    private static final String VOTERS_FILE = "voters.dat";
    private static final String CANDIDATES_FILE = "candidates.dat";
    private static final String RESULTS_FILE = "results.txt";
    
    private static Map<String, Voter> voters = new HashMap<>();
    private static Map<String, Candidate> candidates = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean adminLoggedIn = false;

    public static void main(String[] args) {
        loadData();
        
        System.out.println("🗳️ VOTING SYSTEM");
        System.out.println("---------------");

        while (true) {
            showMainMenu();
        }
    }

    private static void showMainMenu() {
        System.out.println("\n🔹 MAIN MENU");
        System.out.println("1. Voter Login");
        System.out.println("2. Admin Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option (1-3): ");

        int choice = getIntInput(1, 3);
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: voterLogin(); break;
            case 2: adminLogin(); break;
            case 3: saveAndExit();
            default: System.out.println("⚠️ Invalid choice. Please try again.");
        }
    }

    private static void voterLogin() {
        System.out.println("\n👤 VOTER LOGIN");
        System.out.print("Enter Voter ID: ");
        String voterId = scanner.nextLine();
        
        Voter voter = voters.get(voterId);
        if (voter == null) {
            System.out.println("❌ Voter not registered.");
            return;
        }
        
        if (voter.hasVoted()) {
            System.out.println("⚠️ You have already voted.");
            return;
        }
        
        System.out.println("\nWelcome, " + voter.getName() + "!");
        castVote(voter);
    }

    private static void castVote(Voter voter) {
        System.out.println("\n🏛️ AVAILABLE CANDIDATES");
        if (candidates.isEmpty()) {
            System.out.println("No candidates available.");
            return;
        }
        
        List<Candidate> candidateList = new ArrayList<>(candidates.values());
        for (int i = 0; i < candidateList.size(); i++) {
            Candidate c = candidateList.get(i);
            System.out.printf("%d) %s (%s)\n", i+1, c.getName(), c.getParty());
        }
        
        System.out.print("\nEnter candidate number to vote: ");
        int choice = getIntInput(1, candidateList.size());
        scanner.nextLine(); // Consume newline
        
        Candidate selected = candidateList.get(choice-1);
        selected.incrementVotes();
        voter.setVoted();
        
        System.out.println("\n✅ Thank you for voting for " + selected.getName() + "!");
    }

    private static void adminLogin() {
        System.out.println("\n🔐 ADMIN LOGIN");
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();
        
        // Simple password check (in real system, use proper authentication)
        if (!"admin123".equals(password)) {
            System.out.println("❌ Invalid admin password.");
            return;
        }
        
        adminLoggedIn = true;
        System.out.println("\n👑 ADMIN DASHBOARD");
        adminMenu();
    }

    private static void adminMenu() {
        while (adminLoggedIn) {
            System.out.println("\n🔹 ADMIN MENU");
            System.out.println("1. Register New Voter");
            System.out.println("2. Add Candidate");
            System.out.println("3. View Voters List");
            System.out.println("4. View Candidates");
            System.out.println("5. View Election Results");
            System.out.println("6. Save Results to File");
            System.out.println("7. Logout");
            System.out.print("Choose an option (1-7): ");

            int choice = getIntInput(1, 7);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: registerVoter(); break;
                case 2: addCandidate(); break;
                case 3: viewVoters(); break;
                case 4: viewCandidates(); break;
                case 5: viewResults(); break;
                case 6: saveResultsToFile(); break;
                case 7: adminLogout(); break;
                default: System.out.println("⚠️ Invalid choice. Please try again.");
            }
        }
    }

    private static void registerVoter() {
        System.out.println("\n📝 REGISTER NEW VOTER");
        System.out.print("Enter Voter ID: ");
        String voterId = scanner.nextLine();
        
        if (voters.containsKey(voterId)) {
            System.out.println("❌ Voter ID already exists.");
            return;
        }
        
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();
        
        voters.put(voterId, new Voter(voterId, name));
        System.out.println("\n✅ Voter registered successfully!");
    }

    private static void addCandidate() {
        System.out.println("\n➕ ADD CANDIDATE");
        System.out.print("Enter Candidate ID: ");
        String candidateId = scanner.nextLine();
        
        if (candidates.containsKey(candidateId)) {
            System.out.println("❌ Candidate ID already exists.");
            return;
        }
        
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Party/Affiliation: ");
        String party = scanner.nextLine();
        
        candidates.put(candidateId, new Candidate(candidateId, name, party));
        System.out.println("\n✅ Candidate added successfully!");
    }

    private static void viewVoters() {
        System.out.println("\n👥 REGISTERED VOTERS (" + voters.size() + ")");
        if (voters.isEmpty()) {
            System.out.println("No voters registered.");
            return;
        }
        
        voters.values().stream()
            .sorted(Comparator.comparing(Voter::getName))
            .forEach(v -> System.out.printf("%s - %s (%s)\n", 
                v.getVoterId(), v.getName(), v.hasVoted() ? "Voted" : "Not Voted"));
    }

    private static void viewCandidates() {
        System.out.println("\n🏛️ CANDIDATES (" + candidates.size() + ")");
        if (candidates.isEmpty()) {
            System.out.println("No candidates available.");
            return;
        }
        
        candidates.values().stream()
            .sorted(Comparator.comparing(Candidate::getName))
            .forEach(c -> System.out.printf("%s - %s (%s) - Votes: %d\n", 
                c.getCandidateId(), c.getName(), c.getParty(), c.getVotes()));
    }

    private static void viewResults() {
        System.out.println("\n📊 ELECTION RESULTS");
        if (candidates.isEmpty()) {
            System.out.println("No candidates available.");
            return;
        }
        
        List<Candidate> sortedCandidates = new ArrayList<>(candidates.values());
        sortedCandidates.sort((c1, c2) -> Integer.compare(c2.getVotes(), c1.getVotes()));
        
        int totalVotes = sortedCandidates.stream().mapToInt(Candidate::getVotes).sum();
        int votedVoters = (int) voters.values().stream().filter(Voter::hasVoted).count();
        
        System.out.println("Total Registered Voters: " + voters.size());
        System.out.println("Voters Who Cast Votes: " + votedVoters);
        System.out.println("Total Votes Cast: " + totalVotes + "\n");
        
        System.out.println("CANDIDATE RESULTS:");
        for (Candidate c : sortedCandidates) {
            double percentage = totalVotes > 0 ? (c.getVotes() * 100.0 / totalVotes) : 0;
            System.out.printf("%s (%s): %d votes (%.1f%%)\n", 
                c.getName(), c.getParty(), c.getVotes(), percentage);
        }
        
        if (!sortedCandidates.isEmpty()) {
            System.out.println("\n🏆 LEADER: " + sortedCandidates.get(0).getName() + 
                              " with " + sortedCandidates.get(0).getVotes() + " votes");
        }
    }

    private static void saveResultsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_FILE))) {
            writer.println("ELECTION RESULTS");
            writer.println("----------------");
            writer.println("Date: " + new Date());
            writer.println("Total Voters: " + voters.size());
            writer.println("Votes Cast: " + voters.values().stream().filter(Voter::hasVoted).count());
            writer.println();
            
            List<Candidate> sortedCandidates = new ArrayList<>(candidates.values());
            sortedCandidates.sort((c1, c2) -> Integer.compare(c2.getVotes(), c1.getVotes()));
            
            int totalVotes = sortedCandidates.stream().mapToInt(Candidate::getVotes).sum();
            
            writer.println("CANDIDATE RESULTS:");
            for (Candidate c : sortedCandidates) {
                double percentage = totalVotes > 0 ? (c.getVotes() * 100.0 / totalVotes) : 0;
                writer.printf("%s (%s): %d votes (%.1f%%)\n", 
                    c.getName(), c.getParty(), c.getVotes(), percentage);
            }
            
            if (!sortedCandidates.isEmpty()) {
                writer.println("\nWINNER: " + sortedCandidates.get(0).getName());
            }
            
            System.out.println("\n✅ Results saved to " + RESULTS_FILE);
        } catch (IOException e) {
            System.out.println("❌ Error saving results: " + e.getMessage());
        }
    }

    private static void adminLogout() {
        adminLoggedIn = false;
        System.out.println("\n👋 Logged out from admin panel.");
    }

    private static void saveAndExit() {
        saveData();
        System.out.println("\n💾 Data saved. Thank you for using the Voting System!");
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    private static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VOTERS_FILE))) {
            voters = (Map<String, Voter>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("⚠️ Could not load voter data. Starting with empty registry.");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CANDIDATES_FILE))) {
            candidates = (Map<String, Candidate>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("⚠️ Could not load candidate data. Starting with empty list.");
        }
    }

    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(VOTERS_FILE))) {
            oos.writeObject(voters);
        } catch (IOException e) {
            System.out.println("⚠️ Error saving voter data: " + e.getMessage());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CANDIDATES_FILE))) {
            oos.writeObject(candidates);
        } catch (IOException e) {
            System.out.println("⚠️ Error saving candidate data: " + e.getMessage());
        }
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.printf("⚠️ Please enter a number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.print("⚠️ Invalid input. Please enter a number: ");
                scanner.next();
            }
        }
    }
}
