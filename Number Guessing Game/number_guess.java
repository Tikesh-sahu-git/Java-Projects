import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        System.out.println("------------------------");
        System.out.println("  Number Guessing Game  ");
        System.out.println("------------------------");
        
        int minRange = 1;
        int maxRange = 100;
        int maxAttempts = 5;
        int score = 0;
        boolean playAgain = true;
        
        while (playAgain) {
            int secretNumber = random.nextInt(maxRange - minRange + 1) + minRange;
            int attempts = 0;
            boolean hasWon = false;
            
            System.out.printf("\nI'm thinking of a number between %d and %d.%n", minRange, maxRange);
            System.out.printf("You have %d attempts to guess it.%n", maxAttempts);
            
            while (attempts < maxAttempts && !hasWon) {
                System.out.printf("\nAttempt %d/%d - Enter your guess: ", attempts + 1, maxAttempts);
                int guess;
                
                try {
                    guess = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                    continue;
                }
                
                if (guess < minRange || guess > maxRange) {
                    System.out.printf("Please enter a number between %d and %d.%n", minRange, maxRange);
                    continue;
                }
                
                attempts++;
                
                if (guess == secretNumber) {
                    hasWon = true;
                    int pointsEarned = maxAttempts - attempts + 1;
                    score += pointsEarned;
                    System.out.printf("Congratulations! You guessed the number in %d attempts.%n", attempts);
                    System.out.printf("You earned %d points. Total score: %d%n", pointsEarned, score);
                } else if (guess < secretNumber) {
                    System.out.println("Too low! Try a higher number.");
                } else {
                    System.out.println("Too high! Try a lower number.");
                }
            }
            
            if (!hasWon) {
                System.out.printf("\nGame over! The secret number was %d.%n", secretNumber);
            }
            
            System.out.print("\nWould you like to play again? (yes/no): ");
            String playAgainInput = scanner.nextLine().toLowerCase();
            playAgain = playAgainInput.equals("yes") || playAgainInput.equals("y");
        }
        
        System.out.printf("\nThanks for playing! Your final score: %d%n", score);
        scanner.close();
    }
}
