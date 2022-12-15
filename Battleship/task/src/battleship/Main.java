package battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static class Boat{
        List<String> coordinates;
        Boolean sinkStatus;
        Boolean informedOfSinking;

        public Boat(String input1 , String input2){
            // creating List<String>
            List<String> coordinates = new ArrayList<>();
            //adding coordinates
            char firstLetter1 = input1.charAt(0);
            char firstLetter2 = input2.charAt(0);
            int number1 = Integer.parseInt(input1.substring(1));
            int number2 = Integer.parseInt(input2.substring(1));
            if (firstLetter1 == firstLetter2){
                int minimumNumber = Math.min(number1, number2);
                //ADD NEW BOAT
                for (int i = 0 ; i <= Math.abs(number2-number1) ; i++){
                    coordinates.add((firstLetter2)+""+(minimumNumber+i));
                }
            } else {
                char minimumChar = firstLetter1;
                if (firstLetter1>firstLetter2){minimumChar = firstLetter2;}
                for (int i = 0 ; i <= Math.abs(firstLetter2-firstLetter1) ; i++){
                    coordinates.add((Character.toString(minimumChar+i))+""+(number1));
                }
            }
            this.coordinates = coordinates;
            this.sinkStatus = false;
            this.informedOfSinking = false;
        }

        public Boolean getInformedOfSinking() {
            return informedOfSinking;
        }

        public void setInformedOfSinking(Boolean informedOfSinking) {
            this.informedOfSinking = informedOfSinking;
        }

        public List<String> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<String> coordinates) {
            this.coordinates = coordinates;
        }

        public Boolean getSinkStatus() {
            return sinkStatus;
        }

        public void setSinkStatus(Boolean sinkStatus) {
            this.sinkStatus = sinkStatus;
        }

        @Override
        public String toString() {
            return "Boat{" +
                    "coordinates=" + coordinates +
                    ", sinkStatus=" + sinkStatus +
                    ", informedOfSinking=" + informedOfSinking +
                    '}';
        }
    }

    public static void main(String[] args) {
        //initialising various arrays
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

        //Set up of player 1 and set up of boats
        StartingGameBoard(currentPlayer);
        StartingGameBoardAttacker(currentPlayer);

        //add boats
        System.out.println("Player "+currentPlayer+", place your ships to the game field");
        printBoard(currentPlayer);
        for (int boatSize : boatSizes) {
            addBoat(boatSize, currentPlayer);
        }

        //change PLayer to 2
        changePlayer();
        System.out.println("Player "+currentPlayer+", place your ships to the game field");
        //Set up of player 2 and set up of boats
        StartingGameBoard(currentPlayer);
        StartingGameBoardAttacker(currentPlayer);
        printBoard(currentPlayer);
        //add boats
        for (int boatSize : boatSizes) {
            addBoat(boatSize, currentPlayer);
        }
        //Change Player to 1
        changePlayerForShot();

    }
    static int currentPlayer = 1;
/*    public static void clrscr() {
        for (int i = 0; i < 30; i++)
            System.out.print("\n");}*/

    private static void changePlayer(){
        Scanner scanner = new Scanner(System.in);
            System.out.println("Press Enter and pass the move to another player\n");
            scanner.nextLine();

            /*clrscr();*/
            if (currentPlayer == 1){
                currentPlayer = 2;
            } else {
                currentPlayer =1;
            }
        }
    private static void changePlayerForShot(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter and pass the move to another player\n");
        scanner.nextLine();
        if (currentPlayer == 1){
            currentPlayer = 2;
        } else {
            currentPlayer =1;
        }
        printBoardAttacker(currentPlayer);
        System.out.println("---------------------");
        printBoard(currentPlayer);
        System.out.println("Player "+currentPlayer+", it's your turn:");
        takingShot(currentPlayer);
    }

    private static void takingShot(int playerNumber) {
        Scanner sc = new Scanner(System.in);
        List<String> allShotsList = allShotsListBoth.get(playerNumber-1);
        List<String> hitList = hitListBoth.get(playerNumber-1);
        String input = null;

        try {
            input = sc.next("[A-J][1-9]\\b|[A-J]10\\b");
        } catch (Exception e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            takingShot(playerNumber);

        }


        char firstLetter = input.charAt(0);
        int number = Integer.parseInt(input.substring(1));
        int opponentPlayerNumber = playerNumber==1 ? 2 : 1;
        String[][] opponentGameArray = gameArrayBoth.get(opponentPlayerNumber-1);
        String[][] gameArrayAttacker = gameArrayAttackerBoth.get(playerNumber-1);
        String cellAimed = opponentGameArray[firstLetter-'A'+1][number];
        if (hitList.contains(input)){
            String result = switch(gameArrayAttacker[firstLetter-'A'+1][number]){
                case "X" -> "You hit a ship!";
                default -> "You missed!";
            };
            System.out.println(result);
            changePlayerForShot();
        }
        String response = switch (cellAimed){
            case "O" -> "You hit a ship!";
            default -> "You missed. Try again:";
        };
        if (response.equals("You hit a ship!")){
            gameArrayAttacker[firstLetter-'A'+1][number] = "X";
            opponentGameArray[firstLetter-'A'+1][number] = "X";
            hitList.add(input);
        } else {
            gameArrayAttacker[firstLetter-'A'+1][number] = "M";
            //printBoardAttacker(playerNumber);
            System.out.println("You missed!\n");
            opponentGameArray[firstLetter-'A'+1][number] = "M";
            changePlayerForShot();
        }

        //conditional about sinking boat, finishing game, or going for another hit
        updateBoatSinkStatus(playerNumber);

        boolean boatIsSunkStatus = checkBoatIsSunk(playerNumber);
        boolean allBoatSunkStatus = checkIfAllBoatsSunk(playerNumber);

        if (boatIsSunkStatus && allBoatSunkStatus){
            System.out.println("You sank the last ship. You won. Congratulations!");
            //printBoardAttacker(playerNumber);
            sc.close();
            System.exit(1);
        }
        if (boatIsSunkStatus && !allBoatSunkStatus){
            //printBoardAttacker(playerNumber);
            System.out.println("\nYou sank a ship!");
            //System.out.println("\nYou sank a ship! Specify a new target:");
            changePlayerForShot();
        }
        if (!boatIsSunkStatus && !allBoatSunkStatus && response.equals("You hit a ship!")) {
            //printBoardAttacker(playerNumber);
            System.out.println("You hit a ship!");
            //System.out.println("You hit a ship! Try again:");
            changePlayerForShot();
        }

    }

    private static void updateBoatSinkStatus (int playerNumber){
        int opponenetPlayerNumber = playerNumber==1 ? 2:1;
        List<Boat> allBoats = allBoatsBoth.get(opponenetPlayerNumber-1);
        List<String> hitList = hitListBoth.get(playerNumber-1);
        for (Boat boat: allBoats){
            List<String> array = boat.getCoordinates();
            boolean status = true;
            for(String coordinate: array){
                if (!hitList.contains(coordinate) && !boat.getSinkStatus()) {
                    status = false;
                }
            }
            boat.setSinkStatus(status);
        }
    }
    //the function below includes a check for any boat if there is a new boat sunk
    private static boolean checkBoatIsSunk (int playerNumber){
        int opponenetPlayerNumber = playerNumber==1 ? 2:1;
        List<Boat> allBoats = allBoatsBoth.get(opponenetPlayerNumber - 1);
        boolean status = false;
        for(Boat boat: allBoats){
            if(boat.getSinkStatus() && !boat.getInformedOfSinking()){
                boat.setInformedOfSinking(true);
                status = true;
            }
        }
        return status;
    }

    private static boolean checkIfAllBoatsSunk(int playerNumber){
        List<String> hitList = hitListBoth.get(playerNumber-1);
        boolean status = true;
        int sum = 5+4+3+3+2;
        if (hitList.size() < sum){
            status = false;
        }
        return status;
    }

    private static void addBoat(int length, int playerNumber) {
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

        String input1 = null;
        try {
            input1 = sc.next("[A-Z][1-9]\\b|[A-Z]10\\b");
        } catch (Exception e) {
            System.out.println("Error! Wrong coordinate");
            addBoat(length, playerNumber);
        }

        String input2 = null;
        try {
            input2 = sc.next("[A-Z][1-9]\\b|[A-Z]10\\b");
        } catch (Exception e) {
            System.out.println("Error! Wrong coordinate");
            addBoat(length, playerNumber);
        }

        //Input checks
        if (!rowColumnAligned(input1, input2)) {
            System.out.println("Error! Coordinates not arranges in one row or column");
            addBoat(length, playerNumber);
        }
        if (!checkLength(length, input1, input2)) {
            System.out.println("Error! Wrong length!");
            addBoat(length, playerNumber);
        }
        //Adding the boat to the board
        if(rowColumnAligned(input1, input2) && checkLength(length, input1, input2)) {
            addBoatToBoard(length, input1, input2 , playerNumber);
            printBoard(playerNumber);
        }
    }

    private static void addBoatToBoard(int length, String input1, String input2, int playerNumber) {
        char firstLetter1 = input1.charAt(0);
        char firstLetter2 = input2.charAt(0);
        int number1 = Integer.parseInt(input1.substring(1));
        int number2 = Integer.parseInt(input2.substring(1));
        List<Boat> allBoats = allBoatsBoth.get(playerNumber-1);
        //create new Boat and add to allBoats.
        allBoats.add(new Boat(input1,input2));
        String[][] gameArray = gameArrayBoth.get(playerNumber-1);
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

    public static boolean rowColumnAligned(String input1, String input2) {
        //check result are in the same row or column
        char firstLetter1 = input1.charAt(0);
        char firstLetter2 = input2.charAt(0);
        int number1 = Integer.parseInt(input1.substring(1));
        int number2 = Integer.parseInt(input2.substring(1));
        return ((firstLetter1==firstLetter2 && !(number1==number2)) || (number1==number2 && !(firstLetter1==firstLetter2)));
    }

    public static boolean checkLength(int length, String input1 , String input2) {
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

    public static void StartingGameBoard(int playerNumber){
        String[][] gameArray = gameArrayBoth.get(playerNumber-1);
        //First Line
        for (int i = 0 ; i <= 10 ; i++) {
            if (i == 0){
                gameArray[0][i]= " ";
            }
            if(i>0){
                gameArray[0][i]= String.valueOf(i);
            }
        }
        //Rest of the lines
        char leadChar = 'A';
        for(int i = 1 ; i <= 10 ; i++){
            for (int z = 0 ; z <=10 ; z++){
                if (z == 0){
                    gameArray[i][z]= String.valueOf(leadChar);
                    leadChar++;
                }
                if (z > 0){
                    gameArray[i][z]= "~";
                }
            }
        }

    }

    public static void StartingGameBoardAttacker(int playerNumber){
        String[][] gameArrayAttacker =gameArrayAttackerBoth.get(playerNumber-1);
        //First Line
        for (int i = 0 ; i <= 10 ; i++) {
            if (i == 0){
                gameArrayAttacker[0][i]= " ";
            }
            if(i>0){
                gameArrayAttacker[0][i]= String.valueOf(i);
            }
        }
        //Rest of the lines
        char leadChar = 'A';
        for(int i = 1 ; i <= 10 ; i++){
            for (int z = 0 ; z <=10 ; z++){
                if (z == 0){
                    gameArrayAttacker[i][z]= String.valueOf(leadChar);
                    leadChar++;
                }
                if (z > 0){
                    gameArrayAttacker[i][z]= "~";
                }
            }
        }

    }
    public static void printBoard(int playerNumber){
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
    public static void printBoardAttacker(int playerNumber){
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

    static List<List<String>> allShotsListBoth = new ArrayList<>();
    //31 stands for the 2nd 3 size boat
    static int[] boatSizes = {5,4,3,31,2};
    static List<List<Boat>> allBoatsBoth = new ArrayList<>();
    static List<String[][]> gameArrayBoth = new ArrayList<>();
    static List<String[][]> gameArrayAttackerBoth = new ArrayList<>();
    static List<List<String>> hitListBoth = new ArrayList<>();

}
