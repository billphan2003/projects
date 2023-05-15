import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean isGameOver = false;
        String difficulty = "a";
        Minefield game;
        boolean debugMode = false;
        boolean correctMove = false;
        // Initialize these to stop the IDE from yelling at me, these values are unused
        int row = -1;
        int col = -1;
        Scanner s = new Scanner(System.in);
        System.out.println("Welcome to Minesweeper! What difficulty do you want to play at?");
        System.out.println("Easy is a 5x5 with 5 mines, medium is a 9x9 with 12 mines, and hard is a 20x20 with 40 mines.");
        while (!"E".equals(difficulty) && !"M".equals(difficulty) && !"H".equals(difficulty)) {
            System.out.println("Enter 'E' for easy, 'M' for medium, or 'H' for hard:");
            difficulty=s.nextLine();
        }
        switch (difficulty){
            case "E" :
                game = new Minefield(5,5,5);
                break;
            case "M" :
                game = new Minefield(9,9,12);
                break;
            case "H" :
                game = new Minefield(20,20,40);
                break;
            // Also added purely because the IDE was yelling at me, it should never trigger
            default:
                throw new IllegalStateException("Unexpected value: " + difficulty);
        }
        System.out.println("If you want debug mode, enter 'Y'. Entering anything else will begin the normal game.");
        if ("Y".equals(s.nextLine())) {
            debugMode = true;
        }
        System.out.println("What's the first square you'd like to uncover? Enter the coordinates in the form of two" +
                " integers separated by a space.");
        while (!correctMove) {
            try {
                // Take in input and interpret it
                String moveInfo = s.nextLine();
                String[] moveInfoSplit = moveInfo.split(" ",2);
                row = Integer.parseInt(moveInfoSplit[0]);
                col = Integer.parseInt(moveInfoSplit[1]);

                // Throws an error if out of bounds, a is a dummy variable
                Cell a = game.minefield[row][col];
                correctMove = true;
            } catch (Exception e) {
                System.out.println("Please enter a legal answer that fits the format.");
            }
        }
        // Reset correctMove
        correctMove = false;

        // At this point, no flags are used, so flagsLeft is the same as the number of mines
        game.createMines(row,col,game.flagsLeft);
        game.evaluateField();
        if (debugMode) {
            game.printMinefield();
        }

        // Decision on whether to have revealMines happen or not
        System.out.println("If you want the first mine to be revealed automatically, enter 'Y'. Entering anything " +
                "else will continue normally.");
        if ("Y".equals(s.nextLine())) {
            // Need to manually reveal the source to avoid everything being revealed twice
            // This is slightly uglier, but saves slightly on time
            game.minefield[row][col].setRevealed(true);
            game.revealMines(row,col);
        } else {
            game.guess(row, col, false);
        }
        System.out.println(game);

        System.out.println("From now on, any guess you make will have the option to place a flag instead of " +
                "revealing the tile.");

        //Main game loop
        while (!isGameOver) {
            System.out.println("Enter the coordinates of your next guess in the form of two integers separated " +
                    "by a space, with nothing afterwards.");
            while (!correctMove) {
                try {
                    // Take in input and interpret it
                    String moveInfo = s.nextLine();
                    String[] moveInfoSplit = moveInfo.split(" ",2);
                    row = Integer.parseInt(moveInfoSplit[0]);
                    col = Integer.parseInt(moveInfoSplit[1]);

                    // Throws an error if out of bounds
                    // Doesn't make correctMove true if it is pre-revealed, so the loop continues
                    if (game.minefield[row][col].getRevealed()) {
                        System.out.println("This square is already revealed, please guess a different tile.");
                        continue;
                    } else {
                        correctMove = true;
                    }
                } catch (Exception e) {
                    System.out.println("Please enter a legal answer that fits the format.");
                    continue;
                }

                // Check if it's going to be flagged and make the move
                System.out.println("Would you like to have that be a flag? You currently have " + game.flagsLeft +
                        " flags left to place. If so, enter 'Y'. Otherwise, enter anything else.");

                // Will return true if there are flags left, otherwise, time to try again
                correctMove = game.guess(row, col, "Y".equals(s.nextLine()));
            }

            // Reset correctMove
            correctMove = false;

            // Show the new game board
            if (debugMode) {
                game.printMinefield();
            }
            System.out.println(game);
            isGameOver = game.gameOver();
        }
    }
}
