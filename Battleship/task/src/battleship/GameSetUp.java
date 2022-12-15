package battleship;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Data
public class GameSetUp {

    // below are the various lists used in the game
    // in most cases Lists include both players list as a subList
    List<List<String>> allShotsListBoth;
    int[] boatSizes;
    List<List<Boat>> allBoatsBoth;
    List<String[][]> gameArrayBoth;
    List<String[][]> gameArrayAttackerBoth;
    List<List<String>> hitListBoth;
    int currentPlayer;

    //Default constructor for setting up all lists
    public GameSetUp() {
        allShotsListBoth = new ArrayList<>();
        //31 stands for the 2nd 3 size boat
        boatSizes = new int[]{5, 4, 3, 31, 2};
        allBoatsBoth = new ArrayList<>();
        gameArrayBoth = new ArrayList<>();
        gameArrayAttackerBoth = new ArrayList<>();
        hitListBoth = new ArrayList<>();
        currentPlayer = 1;

        //Creation of relevant lists for each Player
        allBoatsBoth.add(new ArrayList<>());
        allBoatsBoth.add(new ArrayList<>());

        gameArrayBoth.add(new String[11][11]);
        gameArrayBoth.add(new String[11][11]);

        gameArrayAttackerBoth.add(new String[11][11]);
        gameArrayAttackerBoth.add(new String[11][11]);

        hitListBoth.add(new ArrayList<>());
        hitListBoth.add(new ArrayList<>());

        allShotsListBoth.add(new ArrayList<>());
        allShotsListBoth.add(new ArrayList<>());
    }

    //Empty Player GameBoard creator (see below image of created board)
    /*    1 2 3 4 5 6 7 8 9 10
        A ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        B ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        C ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        D ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        E ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        F ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        G ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        H ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        I ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
        J ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
    public void emptyGameBoardConstructor (String[][] array){
        //First Line
        for (int i = 0 ; i <= 10 ; i++) {
            if (i == 0){
                array[0][i]= " ";
            }
            if(i>0){
                array[0][i]= String.valueOf(i);
            }
        }
        //Rest of the lines
        char leadChar = 'A';
        for(int i = 1 ; i <= 10 ; i++){
            for (int z = 0 ; z <=10 ; z++){
                if (z == 0){
                    array[i][z]= String.valueOf(leadChar);
                    leadChar++;
                }
                if (z > 0){
                    array[i][z]= "~";
                }
            }
        }
    }
    //Players starting board [in future both StartingGameBoard & StartingGameBoardAttacker can be combined
    public void StartingGameBoard(int playerNumber){
        String[][] gameArray = gameArrayBoth.get(playerNumber-1);
        emptyGameBoardConstructor (gameArray);
    }
    //the below board will be the attacking view of the opponent, similar with the board above.
    public void StartingGameBoardAttacker(int playerNumber){
        String[][] gameArrayAttacker =gameArrayAttackerBoth.get(playerNumber-1);
        //First Line
        emptyGameBoardConstructor (gameArrayAttacker);

    }

    //print the players GameBoard (with their boats, hit, misses received)
    public void printBoard(int playerNumber){
        String [][] gameArray = gameArrayBoth.get(playerNumber-1);
        for (String[] row : gameArray){
            for (int i = 0 ; i < row.length ; i++){
                if (i == row.length-1){
                    System.out.println(row[i]+"");
                } else {
                    System.out.print(row[i]+" ");
                }
            }
        }
    }

    //print the players attacking GameBoard (with player's hits and/or misses on the opponents boats)
    public void printBoardAttacker(int playerNumber){
        String[][] gameArrayAttacker = gameArrayAttackerBoth.get(playerNumber-1);
        for (String[] row : gameArrayAttacker){
            for (int i = 0 ; i < row.length ; i++){
                if (i == row.length-1){
                    System.out.println(row[i]+"");
                } else {
                    System.out.print(row[i]+" ");
                }
            }
        }
    }

    //set up of players boats. LOOP
    public void setUpPlayer (int currentPlayer){
        System.out.println("Player "+currentPlayer+", place your ships to the game field");
        StartingGameBoard(currentPlayer);
        StartingGameBoardAttacker(currentPlayer);
        printBoard(currentPlayer);
        for (int boatSize : boatSizes) {
            addBoat(boatSize, currentPlayer);
        }
    }

    //Adding boat loop to go through all the boats sizes and types
    private void addBoat(int length, int playerNumber) {
        Scanner sc = new Scanner(System.in);
        String message = switch (length) {
            case 5 -> "Enter the coordinates of the Aircraft Carrier (5 cells):";
            case 4 -> "Enter the coordinates of the Battleship (4 cells):";
            case 3 -> "Enter the coordinates of the Submarine (3 cells):";
            case 31 -> "Enter the coordinates of the Cruiser (3 cells):";
            case 2 -> "Enter the coordinates of the Destroyer (2 cells):";
            default -> "No boat number added";
        };
        System.out.println(message);
        if (length == 31) {
            length = 3;
        }

        //starting position of  boat
        //input is checked for A1 and Z10 format
        String input1 = null;
        try {
            input1 = sc.next("[A-Z][1-9]\\b|[A-Z]10\\b");
        } catch (Exception e) {
            System.out.println("Error! Wrong coordinate");
            addBoat(length, playerNumber);
        }

        //Ending position of  boat
        //input is checked for A1 and Z10 format
        String input2 = null;
        try {
            input2 = sc.next("[A-Z][1-9]\\b|[A-Z]10\\b");
        } catch (Exception e) {
            System.out.println("Error! Wrong coordinate");
            addBoat(length, playerNumber);
        }

        //Input checks include that boats are set in a column or row, not diagonally
        if (input1!= null && input2 != null && !rowColumnAligned(input1, input2)) {
            System.out.println("Error! Coordinates not arranges in one row or column");
            addBoat(length, playerNumber);
        }
        //length of boat is in accordance with current boat size
        if (input1!= null && input2 != null &&!checkLength(length, input1, input2)) {
            System.out.println("Error! Wrong length!");
            addBoat(length, playerNumber);
        }
        //Adding the boat to the board afer passing all checks
        if(input1!= null && input2 != null && rowColumnAligned(input1, input2) && checkLength(length, input1, input2)) {
            //boat added to board
            addBoatToBoard(length, input1, input2 , playerNumber);
            //board is printed
            printBoard(playerNumber);
        }
    }

    //boat added to board
    private void addBoatToBoard(int length, String input1, String input2, int playerNumber) {
        //broken down input
        char firstLetter1 = input1.charAt(0);
        char firstLetter2 = input2.charAt(0);
        int number1 = Integer.parseInt(input1.substring(1));
        int number2 = Integer.parseInt(input2.substring(1));

        //getting Player's boat list
        List<Boat> allBoats = allBoatsBoth.get(playerNumber-1);
        //create new Boat and add to allBoats.
        allBoats.add(new Boat(input1,input2));


        String[][] gameArray = gameArrayBoth.get(playerNumber-1);
        //if checks if boat is in row first or else-column
        if (firstLetter1 == firstLetter2){
            //create new Boat and add to allBoats.
            int minimumNumber = Math.min(number1, number2);
            //CHECK THAT NO BOAT IS IN PLACE OR AROUND
            for (int i = 0 ; i <= Math.abs(number2-number1) ; i++){
                if(!gameArray[firstLetter2-'A'+1][minimumNumber+i].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //checking parallel top
                if(!gameArray[Math.max(firstLetter2-'A',1)][minimumNumber+i].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //checking parallel bottom
                if(!gameArray[Math.min(firstLetter2-'A'+2,10)][minimumNumber+i].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //checking right
                if(!gameArray[firstLetter2-'A'+1][Math.min(minimumNumber+i+1,10)].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //checking left
                if(!gameArray[firstLetter2-'A'+1][Math.max(minimumNumber+i-1,1)].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
            }
            //ADD NEW BOAT
            for (int i = 0 ; i <= Math.abs(number2-number1) ; i++){
                gameArray[firstLetter2-'A'+1][minimumNumber+i] = "O";
            }
        } else {
            //create new Boat and add to allBoats.
            char minimumChar = firstLetter1;
            if (firstLetter1>firstLetter2){minimumChar = firstLetter2;}
            //CHECK THAT NO BOAT IS IN PLACE OR AROUND
            for (int i = 0 ; i <= Math.abs(firstLetter2-firstLetter1) ; i++){
                int index1 = minimumChar-'A'+1+i;
                int index2 = number1;
                if(!gameArray[index1][index2].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //check above
                if(!gameArray[Math.max(index1-1,1)][index2].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //check below
                if(!gameArray[Math.min(index1+1,10)][index2].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //check right
                if(!gameArray[index1][Math.min(index2+1,10)].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
                //check left
                if(!gameArray[index1][Math.max(index2-1,1)].equals("~")){
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    addBoat(length, playerNumber);
                }
            }
            //ADD NEW BOAT
            for (int i = 0 ; i <= Math.abs(firstLetter2-firstLetter1) ; i++){
                gameArray[minimumChar-'A'+1+i][number1] = "O";
            }
        }

    }

    //checking row and column boat alignment. no diagonal positioning of boars allowed
    public boolean rowColumnAligned(String input1, String input2) {
        //check result are in the same row or column
        char firstLetter1 = input1.charAt(0);
        char firstLetter2 = input2.charAt(0);
        int number1 = Integer.parseInt(input1.substring(1));
        int number2 = Integer.parseInt(input2.substring(1));
        return ((firstLetter1==firstLetter2 && !(number1==number2)) || (number1==number2 && !(firstLetter1==firstLetter2)));
    }

    //checking the boat length
    public boolean checkLength(int length, String input1 , String input2) {
        boolean lengthStatus = false;
        char firstLetter1 = input1.charAt(0);
        char firstLetter2 = input2.charAt(0);
        int number1 = Integer.parseInt(input1.substring(1));
        int number2 = Integer.parseInt(input2.substring(1));
        if (firstLetter1 == firstLetter2){
            lengthStatus = (Math.abs(number2-number1)+1==length);
        } else {
            lengthStatus = (Math.abs(firstLetter2-firstLetter1)+1==length);
        }
        return lengthStatus;
    }

}
