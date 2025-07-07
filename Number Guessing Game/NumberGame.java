import java.util.Scanner;
import java.util.Random;

public class NumberGame {
    public static void main(String[] args) {
        // Scanner for reading user input
        Scanner scanner = new Scanner(System.in);
        // Random object to generate the number to guess
        Random random = new Random();
        
        // Variables to keep track of game stats
        int totalRounds = 0;
        int totalWins = 0;
        boolean playAgain = true;
        
        // Welcome message
        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("I'm thinking of a number between 1 and 100.");
        
        // Main game loop
        while (playAgain) {
            totalRounds++;  // Increment the number of rounds
            int numberToGuess = random.nextInt(100) + 1;  // Generate a random number between 1 and 100
            int attempts = 0;  // Track number of attempts for current round
            int maxAttempts = 10;  // Max allowed attempts
            boolean hasWon = false;  // Track if the user has won this round
            
            System.out.println("\nRound " + totalRounds + " - You have " + maxAttempts + " attempts.");
            
            // Loop for user guesses
            while (attempts < maxAttempts && !hasWon) {
                System.out.print("Enter your guess: ");
                int userGuess;
                
                try {
                    userGuess = scanner.nextInt();  // Read user's guess
                } catch (Exception e) {
                    // Handle non-integer input
                    System.out.println("Please enter a valid number!");
                    scanner.next();  // Clear invalid input
                    continue;
                }
                
                attempts++;  // Increment the attempt count
                
                // Check if guess is correct
                if (userGuess == numberToGuess) {
                    System.out.println("Congratulations! You guessed the number in " + attempts + " attempts!");
                    hasWon = true;
                    totalWins++;  // Increment win count
                } else if (userGuess < numberToGuess) {
                    System.out.println("Too low! Attempts left: " + (maxAttempts - attempts));
                } else {
                    System.out.println("Too high! Attempts left: " + (maxAttempts - attempts));
                }
            }
            
            // If player did not guess correctly in given attempts
            if (!hasWon) {
                System.out.println("Sorry, you've used all your attempts. The number was " + numberToGuess + ".");
            }
            
            // Ask player if they want to play again
            System.out.print("\nWould you like to play again? (yes/no): ");
            String playAgainInput = scanner.next().toLowerCase();
            playAgain = playAgainInput.equals("yes") || playAgainInput.equals("y");
        }
        
        // Game summary after exiting the loop
        System.out.println("\nGame Over!");
        System.out.println("Rounds played: " + totalRounds);
        System.out.println("Rounds won: " + totalWins);
        System.out.println("Win rate: " + ((double)totalWins / totalRounds * 100) + "%");
        
        // Close the scanner
        scanner.close();
    }
}
