package battleship;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class Boat{
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
}
