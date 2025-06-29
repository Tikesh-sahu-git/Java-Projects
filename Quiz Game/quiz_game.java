import java.util.*;
import java.io.*;

class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private int correctAnswer;
    private String explanation;

    public Question(String questionText, List<String> options, int correctAnswer, String explanation) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public boolean isCorrect(int selectedOption) {
        return selectedOption == correctAnswer;
    }

    public String getFormattedQuestion() {
        StringBuilder sb = new StringBuilder();
        sb.append(questionText).append("\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(") ").append(options.get(i)).append("\n");
        }
        return sb.toString();
    }

    public String getExplanation() {
        return explanation;
    }
}

public class JavaQuizGame {
    private static final String QUESTIONS_FILE = "java_questions.dat";
    private static List<Question> questions = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int score = 0;
    private static String currentPlayer = "";

    public static void main(String[] args) {
        loadQuestions();
        
        System.out.println("🧠 JAVA KNOWLEDGE QUIZ GAME");
        System.out.println("---------------------------");
        
        System.out.print("Enter your name: ");
        currentPlayer = scanner.nextLine();
        
        startQuiz();
        showResults();
        saveHighScore();
    }

    private static void startQuiz() {
        System.out.println("\n⚡ The quiz contains " + questions.size() + " questions. Good luck!\n");
        
        Collections.shuffle(questions);
        
        for (int i = 0; i < Math.min(10, questions.size()); i++) {
            Question currentQuestion = questions.get(i);
            System.out.println("Question " + (i + 1) + ":");
            System.out.println(currentQuestion.getFormattedQuestion());
            
            int selectedOption = getValidAnswer(currentQuestion.options.size());
            
            if (currentQuestion.isCorrect(selectedOption - 1)) {
                System.out.println("✅ Correct! " + currentQuestion.getExplanation() + "\n");
                score++;
            } else {
                System.out.println("❌ Incorrect! " + currentQuestion.getExplanation() + "\n");
            }
        }
    }

    private static int getValidAnswer(int maxOption) {
        while (true) {
            System.out.print("Your answer (1-" + maxOption + "): ");
            try {
                int answer = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (answer >= 1 && answer <= maxOption) {
                    return answer;
                }
                System.out.println("⚠️ Please enter a number between 1 and " + maxOption);
            } catch (InputMismatchException e) {
                System.out.println("⚠️ Invalid input. Please enter a number.");
                scanner.next(); // Clear invalid input
            }
        }
    }

    private static void showResults() {
        System.out.println("🎯 QUIZ COMPLETE!");
        System.out.println("Player: " + currentPlayer);
        System.out.println("Score: " + score + "/" + Math.min(10, questions.size()));
        
        double percentage = (double) score / Math.min(10, questions.size()) * 100;
        System.out.printf("Percentage: %.1f%%\n", percentage);
        
        if (percentage >= 80) {
            System.out.println("🏆 Excellent! You're a Java expert!");
        } else if (percentage >= 50) {
            System.out.println("👍 Good job! You know Java well!");
        } else {
            System.out.println("📚 Keep learning! Check out Java documentation to improve.");
        }
    }

    private static void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("highscores.txt", true))) {
            writer.println(currentPlayer + ": " + score + "/" + Math.min(10, questions.size()));
            System.out.println("\n🏅 Your score has been saved to highscores.txt");
        } catch (IOException e) {
            System.out.println("⚠️ Could not save high score: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadQuestions() {
        // Default questions if file doesn't exist
        if (questions.isEmpty()) {
            questions.add(new Question(
                "Which of these is NOT a primitive data type in Java?",
                Arrays.asList("int", "String", "boolean", "double"),
                1,
                "String is a class, not a primitive type. The 8 primitive types are: byte, short, int, long, float, double, boolean, char."
            ));
            
            questions.add(new Question(
                "What is the default value of a boolean variable in Java?",
                Arrays.asList("true", "false", "null", "0"),
                1,
                "The default value of a boolean is false. For objects it's null, for numbers it's 0."
            ));
            
            questions.add(new Question(
                "Which keyword is used to create an instance of a class?",
                Arrays.asList("new", "this", "instance", "class"),
                0,
                "The 'new' keyword allocates memory for a new object and calls the constructor."
            ));
            
            questions.add(new Question(
                "What is the superclass of all classes in Java?",
                Arrays.asList("Object", "Class", "Main", "Super"),
                0,
                "The Object class is at the top of the class hierarchy. All classes inherit from it directly or indirectly."
            ));
            
            questions.add(new Question(
                "Which collection implements a FIFO (First-In-First-Out) behavior?",
                Arrays.asList("ArrayList", "HashSet", "LinkedList", "Queue"),
                3,
                "Queue interface represents FIFO structure. LinkedList implements Queue, but Queue is the more general answer."
            ));
            
            questions.add(new Question(
                "What does JVM stand for?",
                Arrays.asList("Java Virtual Machine", "Java Variable Method", "Java Verified Machine", "Java Visual Modifier"),
                0,
                "JVM is the Java Virtual Machine that executes compiled Java bytecode."
            ));
            
            questions.add(new Question(
                "Which access modifier provides the most restrictive access?",
                Arrays.asList("public", "protected", "default (package-private)", "private"),
                3,
                "private is most restrictive, then default, then protected, then public."
            ));
            
            questions.add(new Question(
                "What is the correct way to declare a constant in Java?",
                Arrays.asList("const int SIZE = 5;", "final int SIZE = 5;", "static int SIZE = 5;", "constant int SIZE = 5;"),
                1,
                "The 'final' keyword makes a variable constant. Java doesn't have a 'const' keyword like some languages."
            ));
            
            questions.add(new Question(
                "Which exception is thrown when dividing by zero?",
                Arrays.asList("NullPointerException", "ArithmeticException", "DivideByZeroException", "NumberFormatException"),
                1,
                "ArithmeticException is thrown for divide by zero operations with integers."
            ));
            
            questions.add(new Question(
                "What is the correct signature for the main method?",
                Arrays.asList("public static int main(String[] args)", 
                             "public void main(String[] args)", 
                             "public static void main(String[] args)", 
                             "private static void main(String[] args)"),
                2,
                "The main method must be public, static, void, and take a String array parameter."
            ));
        }
        
        // Try to load additional questions from file
        File file = new File(QUESTIONS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(QUESTIONS_FILE))) {
                List<Question> loadedQuestions = (List<Question>) ois.readObject();
                questions.addAll(loadedQuestions);
                System.out.println("📚 Loaded " + loadedQuestions.size() + " additional questions from file.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("⚠️ Could not load additional questions: " + e.getMessage());
            }
        }
    }
}
