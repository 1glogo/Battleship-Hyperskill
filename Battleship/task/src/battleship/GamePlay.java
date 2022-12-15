package battleship;

import java.util.List;
import java.util.Scanner;


public class GamePlay {
    //Bringing in the GameSetUp to the GamePlay
    GameSetUp gameSetUp = new GameSetUp();
    List<List<String>> allShotsListBoth = gameSetUp.getAllShotsListBoth();
    int[] boatSizes = gameSetUp.getBoatSizes();
    List<List<Boat>> allBoatsBoth = gameSetUp.getAllBoatsBoth();
    List<String[][]> gameArrayBoth =gameSetUp.getGameArrayBoth();
    List<String[][]> gameArrayAttackerBoth = gameSetUp.getGameArrayAttackerBoth();
    List<List<String>> hitListBoth = gameSetUp.getHitListBoth();
    int currentPlayer = gameSetUp.getCurrentPlayer();

    //changing current player at setUp
    //two methods provided due to different "sout" output requested in testing
    private void changePlayer(){
        Scanner scanner = new Scanner(System.in);
        //awaiting ENTER to change user
        System.out.println("Press Enter and pass the move to another player\n");
        scanner.nextLine();
        //changing of current Player
        if (currentPlayer == 1){
            currentPlayer = 2;
        } else {
            currentPlayer =1;
        }
    }

    //changing current player at gamePlay (Shooting)
    private void changePlayerForShot(){
        //awaiting ENTER to change user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter and pass the move to another player\n");
        scanner.nextLine();
        //changing of current Player
        if (currentPlayer == 1){
            currentPlayer = 2;
        } else {
            currentPlayer =1;
        }
        //Player's up-to-date attacking board
        gameSetUp.printBoardAttacker(currentPlayer);
        System.out.println("---------------------");
        //Player's up-to-date own board
        gameSetUp.printBoard(currentPlayer);
        System.out.println("Player "+currentPlayer+", it's your turn:");
        //moving to taking the shot
        takingShot(currentPlayer);
    }

    //shooting the opponents boats
    private void takingShot(int playerNumber) {
        Scanner sc = new Scanner(System.in);
        //current player hit list
        List<String> hitList = hitListBoth.get(playerNumber-1);

        //requesting input of shot
        String input = null;
        try {
            input = sc.next("[A-J][1-9]\\b|[A-J]10\\b");
        } catch (Exception e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            takingShot(playerNumber);
        }

        //Breaking down input in order to work with it below
        char firstLetter = input.charAt(0);
        int number = Integer.parseInt(input.substring(1));

        //finding opponent player in order to update opponents game Array along with current players
        int opponentPlayerNumber = playerNumber==1 ? 2 : 1;
        String[][] opponentGameArray = gameArrayBoth.get(opponentPlayerNumber-1);
        String[][] gameArrayAttacker = gameArrayAttackerBoth.get(playerNumber-1);

        //reviewing the shot against previous shots taken by player
        //"X" would mean that a shot has already been taken there, therefore
        //"You hit a ship!" comes back, otherwise a miss
        String cellAimed = opponentGameArray[firstLetter-'A'+1][number];
        if (hitList.contains(input)){
            String result = switch(gameArrayAttacker[firstLetter-'A'+1][number]){
                case "X" -> "You hit a ship!";
                default -> "You missed!";
            };
            System.out.println(result);
            //Moving on to the next player
            changePlayerForShot();
        }

        //reviewing the shot against the opponents ship positions
        //"O" would mean a ship position, therefore,
        //"You hit a ship!" comes back, otherwise a miss
        String response = switch (cellAimed){
            case "O" -> "You hit a ship!";
            default -> "You missed. Try again:";
        };
        if (response.equals("You hit a ship!")){
            //update player's gameArray and opponents
            gameArrayAttacker[firstLetter-'A'+1][number] = "X";
            opponentGameArray[firstLetter-'A'+1][number] = "X";
            //add hit to hitList for tracking how many boats have been hit.
            hitList.add(input);
            //due to hit moving on to the next players happen further below after
            //checking conditions of boat sinking and/or game end.
        } else {
            System.out.println("You missed!\n");
            //update player's gameArray and opponents
            gameArrayAttacker[firstLetter-'A'+1][number] = "M";
            opponentGameArray[firstLetter-'A'+1][number] = "M";
            //move to next player
            changePlayerForShot();
        }

        //conditional about sinking boat, finishing game, or going for another hit
        updateBoatSinkStatus(playerNumber);

        boolean boatIsSunkStatus = checkBoatIsSunk(playerNumber);
        boolean allBoatSunkStatus = checkIfAllBoatsSunk(playerNumber);

        //checking for game end
        if (boatIsSunkStatus && allBoatSunkStatus){
            System.out.println("You sank the last ship. You won. Congratulations!");
            sc.close();
            System.exit(1);
        }

        //checking for boat sinking & game continuation
        if (boatIsSunkStatus && !allBoatSunkStatus){
            //printBoardAttacker(playerNumber);
            System.out.println("\nYou sank a ship!");
            //System.out.println("\nYou sank a ship! Specify a new target:");
            changePlayerForShot();
        }

        //checking for simple hit and game continuation.
        if (!boatIsSunkStatus && !allBoatSunkStatus && response.equals("You hit a ship!")) {
            System.out.println("You hit a ship!");
            changePlayerForShot();
        }

    }

    //Checking every previously not-sunk boat if it is sunk due to latest move
    private void updateBoatSinkStatus (int playerNumber){
        //checking opponents boats & current players hitList
        int opponentPlayerNumber = playerNumber==1 ? 2:1;
        List<Boat> allBoats = allBoatsBoth.get(opponentPlayerNumber-1);
        List<String> hitList = hitListBoth.get(playerNumber-1);

        //for Each boat check, that has NOT been previously sank
        // that not all coordinates are part of the hitList,
        //if yes, boat is sank and status is updated accordingly
        //if not the status is set to false
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
    private boolean checkBoatIsSunk (int playerNumber){
        //getting opponents boats
        int opponentPlayerNumber = playerNumber==1 ? 2:1;
        List<Boat> allBoats = allBoatsBoth.get(opponentPlayerNumber - 1);
        boolean status = false;

        //check that each boat which has NOT been previously sank
        // if they have now been sunk, if yes return true and update the
        //informedOfSinking boolean in the boat
        for(Boat boat: allBoats){
            if(boat.getSinkStatus() && !boat.getInformedOfSinking()){
                boat.setInformedOfSinking(true);
                status = true;
            }
        }
        return status;
    }

    //checking status of all boats and if they have been sunk for a player
    private boolean checkIfAllBoatsSunk(int playerNumber){
        //checking whether the number of hits matches the total boatSize
        List<String> hitList = hitListBoth.get(playerNumber-1);
        boolean status = true;
        int sum = 5+4+3+3+2;
        if (hitList.size() < sum){
            status = false;
        }
        return status;
    }

    //starting Game
    public void gameStart(){
        //Set up of player 1 and set up of boats
        gameSetUp.setUpPlayer(currentPlayer);
        //change PLayer to 2
        changePlayer();
        gameSetUp.setUpPlayer(currentPlayer);
        //Change Player to 1 and then loop between the two until there is a winner
        //there is no draw
        changePlayerForShot();
    }

}
