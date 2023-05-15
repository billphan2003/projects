import java.awt.Color;
import java.util.Random;

public class Minefield {
    /**
    Global Section
    */
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREY_BG = "\u001b[0m";

    /**
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Cell[][] minefield;
    public int flagsLeft;
    public Minefield(int rows, int columns, int flags) {
        minefield = new Cell[rows][columns];

        // Uses a double for loop
        for (int i=0; i<minefield.length; i++) {
            for (int j=0; j<minefield[0].length; j++) {
                minefield[i][j] = new Cell(false, "–");
            }
        }
        this.flagsLeft = flags;
    }

    /**
     * evaluateField
     *
     * @function When a mine is found in the field, calculate the surrounding 9x9 tiles values. If a mine is found, increase the count for the square.
     */
    public void evaluateField() {
        // Set every non-mine to zero for the next part
        for (int i=0; i<minefield.length; i++) {
            for (int j=0; j<minefield[0].length; j++) {
                if (!minefield[i][j].getStatus().equals("M")) {
                    minefield[i][j].setStatus("0");
                }
            }
        }

        // Set every non-mine to the right value
        for (int i=0; i<minefield.length; i++) {
            for (int j=0; j<minefield[0].length; j++) {
                if (minefield[i][j].getStatus().equals("M")) {
                    // Create the bounds for the square around a mine
                    int iLowBound = Math.max(i - 1, 0);
                    int iUpBound = Math.min(i + 1, minefield.length - 1);
                    int jLowBound = Math.max(j - 1, 0);
                    int jUpBound = Math.min(j + 1, minefield[0].length - 1);

                    // Increase everything not a mine by 1 if it has a nearby mine
                    for (int k=iLowBound; k<=iUpBound; k++) {
                        for (int l=jLowBound; l<=jUpBound; l++) {
                            if (!minefield[k][l].getStatus().equals("M")) {
                                minefield[k][l].setStatus("" + (Integer.parseInt(minefield[k][l].getStatus())+1));
                            }
                        }
                    }
                }
            }
        }

        // Recolor the numbers and mines
        for (Cell[] cells : minefield) {
            for (Cell cell : cells) {
                String tempStatus = cell.getStatus();
                switch (tempStatus) {
                    case "0":
                        cell.setStatus(ANSI_YELLOW);
                        break;
                    case "1":
                        cell.setStatus(ANSI_BLUE);
                        break;
                    case "2":
                        cell.setStatus(ANSI_GREEN);
                        break;
                    case "3":
                        cell.setStatus(ANSI_RED);
                        break;
                    case "4":
                        cell.setStatus(ANSI_PURPLE);
                        break;
                    case "5":
                        cell.setStatus(ANSI_YELLOW);
                        break;
                    case "6":
                        cell.setStatus(ANSI_GREEN);
                        break;
                    case "7":
                        cell.setStatus(ANSI_CYAN);
                        break;
                    case "8":
                        cell.setStatus(ANSI_PURPLE);
                        break;
                    case "M":
                        cell.setStatus(ANSI_RED_BRIGHT);
                }
                cell.setStatus(cell.getStatus()+tempStatus+ANSI_GREY_BG);
            }
        }
    }

    /**
     * createMines
     *
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random rand = new Random();
        // Generate the given number of mines
        for (int i=0; i<mines;i++) {
            int a = x;
            int b = y;
            // Keep on generating new positions until they don't overlap with the start or other mines
            while ((a == x && b == y) || minefield[a][b].getStatus().equals("M")) {
                a = rand.nextInt(minefield.length);
                b = rand.nextInt(minefield[0].length);
            }
            minefield[a][b].setStatus("M");
        }
    }

    /**
     * guess
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        if (flag) {
            if (flagsLeft>0) {
                minefield[x][y].setStatus(ANSI_RED_BRIGHT+"F"+ANSI_GREY_BG);
                flagsLeft--;
            } else {
                // If out of flags, the returned false will ensure that there's a total redo
                System.out.println("Out of flags, please try something else.");
                return false;
            }
        } else {
            if (minefield[x][y].getStatus().equals(ANSI_RED_BRIGHT+"M"+ANSI_GREY_BG)) {
                System.out.println("You hit a mine! Game Over.");
                // Reveals all mines, as in traditional minesweeper
                for (Cell[] cells : minefield) {
                    for (Cell cell : cells) {
                        if (cell.getStatus().equals(ANSI_RED_BRIGHT+"M"+ANSI_GREY_BG)) {
                            cell.setRevealed(true);
                        }
                    }
                }
            } else if (minefield[x][y].getStatus().equals(ANSI_YELLOW+"0"+ANSI_GREY_BG)) {
                this.revealZeroes(x,y);
            }
        }
        minefield[x][y].setRevealed(true);
        return true;
    }

    /**
     * gameOver
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otherwise return true.
     */
    public boolean gameOver() {
        // Uses a double for-each loop to check for revealed mine cells
        for (Cell[] cells : minefield) {
            for (Cell cell : cells) {
                if (cell.getRevealed() && cell.getStatus().equals(ANSI_RED_BRIGHT+"M"+ANSI_GREY_BG)) {
                    return true;
                }
            }
        }

        // Uses a double for-each loop to check for unrevealed cells
        for (Cell[] cells : minefield) {
            for (Cell cell : cells) {
                if (!cell.getRevealed()) {
                    return false;
                }
            }
        }

        // Otherwise return that the game is over, and that the player won
        System.out.println("You won! Congratulations!");
        return true;
    }

    /**
     * revealZeroes
     *
     * This method should follow the pseudocode given.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */

    Stack1Gen<int[]> stack = new Stack1Gen<>();
    public void revealZeroes(int x, int y) {
        // Add the current thing to the stack
        stack.push(new int[]{x, y});

        // Run as long as there's still stuff in the stack
        while (!stack.isEmpty()) {
            // Get the item on top
            int[] tempPop = stack.pop();
            // Reveal the topmost
            minefield[tempPop[0]][tempPop[1]].setRevealed(true);

            // Add everything to the north, west, east, and south, if it's viable
            if (tempPop[0]>0 && !minefield[tempPop[0]-1][tempPop[1]].getRevealed()) {
                if (minefield[tempPop[0]-1][tempPop[1]].getStatus().equals(ANSI_YELLOW+"0"+ANSI_GREY_BG)) {
                    stack.push(new int[]{tempPop[0] - 1, tempPop[1]});
                } else {
                    // If a neighbor isn't a zero, still reveal it, but stop there
                    minefield[tempPop[0]-1][tempPop[1]].setRevealed(true);
                }
            }
            if (tempPop[1]>0 && !minefield[tempPop[0]][tempPop[1]-1].getRevealed()) {
                if (minefield[tempPop[0]][tempPop[1]-1].getStatus().equals(ANSI_YELLOW+"0"+ANSI_GREY_BG)) {
                    stack.push(new int[]{tempPop[0], tempPop[1] - 1});
                } else {minefield[tempPop[0]][tempPop[1]-1].setRevealed(true);}
            }
            if (tempPop[1]<minefield[0].length-1 && !minefield[tempPop[0]][tempPop[1]+1].getRevealed()) {
                if (minefield[tempPop[0]][tempPop[1]+1].getStatus().equals(ANSI_YELLOW+"0"+ANSI_GREY_BG)) {
                    stack.push(new int[]{tempPop[0], tempPop[1] + 1});
                } else {minefield[tempPop[0]][tempPop[1]+1].setRevealed(true);}
            }
            if (tempPop[0]<minefield.length-1 && !minefield[tempPop[0]+1][tempPop[1]].getRevealed()) {
                if (minefield[tempPop[0]+1][tempPop[1]].getStatus().equals(ANSI_YELLOW+"0"+ANSI_GREY_BG)) {
                    stack.push(new int[]{tempPop[0] + 1, tempPop[1]});
                } else {minefield[tempPop[0]+1][tempPop[1]].setRevealed(true);}
            }
        }
    }

    /**
     * revealMines
     *
     * This method should follow the pseudocode given.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */

    Q1Gen<int[]> queue = new Q1Gen<>();
    public void revealMines(int x, int y) {
        // Add the current thing to the stack
        queue.add(new int[]{x, y});
        while (true) {
            // Get the item in front
            int[] tempPop = queue.remove();

            // Reveal the current item
            minefield[tempPop[0]][tempPop[1]].setRevealed(true);

            // If the current item is a mine, no more adding to the queue
            if (minefield[tempPop[0]][tempPop[1]].getStatus().equals(ANSI_RED_BRIGHT+"M"+ANSI_GREY_BG)) {
                // Flag the found mine
                minefield[tempPop[0]][tempPop[1]].setStatus(ANSI_RED_BRIGHT+"F"+ANSI_GREY_BG);
                break;
            }

            // Look at the direct neighbors
            if (tempPop[0]>0 && !minefield[tempPop[0]-1][tempPop[1]].getRevealed()) {
                // Add them to the queue
                queue.add(new int[]{tempPop[0] - 1, tempPop[1]});
            }
            if (tempPop[1]>0 && !minefield[tempPop[0]][tempPop[1]-1].getRevealed()) {
                queue.add(new int[]{tempPop[0], tempPop[1] - 1});
            }
            if (tempPop[1]<minefield[0].length-1 && !minefield[tempPop[0]][tempPop[1]+1].getRevealed()) {
                queue.add(new int[]{tempPop[0], tempPop[1] + 1});
            }
            if (tempPop[0]<minefield.length-1 && !minefield[tempPop[0]+1][tempPop[1]].getRevealed()) {
                queue.add(new int[]{tempPop[0] + 1, tempPop[1]});
            }
        }

        // Additionally, use the zeroes in the queue to trigger revealZeroes
        while (queue.length()>0) {
            // Take the front item in the queue
            int[] tempPop = queue.remove();
            // Check if it's a zero
            if (minefield[tempPop[0]][tempPop[1]].getStatus().equals(ANSI_YELLOW+"0"+ANSI_GREY_BG)) {
                // If it is, call revealZeroes on it
                revealZeroes(tempPop[0], tempPop[1]);
            }
        }
    }

    /**
     * printMinefield
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected. 
     */
    public void printMinefield() {
        String result = "   ";

        // Does the top row
        for (int i = 0; i < minefield.length; i++) {
            if (i < 9) {
                result += i + "  ";
            } else {
                result += i + " ";
            }
        }

        // Uses a nested for loop
        for (int i = 0; i < minefield.length; i++) {
            // Beginning of line spacing
            if (i < 10) {
                result += "\n" + i + "  ";
            } else {
                result += "\n" + i + " ";
            }
            for (int j = 0; j < minefield[0].length; j++) {
                result += minefield[i][j].getStatus() + "  ";
            }
        }
        result += "\n";
        System.out.println(result);
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        String result = "   ";

        // Does the top row
        for (int i = 0; i < minefield.length; i++) {
            if (i < 9) {
                result += i + "  ";
            } else {
                result += i + " ";
            }
        }

        // Uses a for-each loop within a for loop
        for (int i = 0; i<minefield.length; i++) {
            // Beginning of line spacing
            if (i < 10) {
                result += "\n" + i + "  ";
            } else {
                result += "\n" + i + " ";
            }
            for (int j = 0; j<minefield[0].length; j++) {
                if (minefield[i][j].getRevealed()) {
                    result += minefield[i][j].getStatus() + "  ";
                } else {
                    result += "–  ";
                }
            }
        }
        result += "\n";
        return result;
    }
}
