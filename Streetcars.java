// Child class of Vehicle that holds the information about every Streetcar defined in the streetcar.txt file
public class Streetcars extends Vehicle {

    // Variables and accessor methods are inherited from the Vehicle class
    // Constructor class takes an array of Strings that is organizes
    Streetcars(String[] transportInfo, String description) {
        this.description = description.replace(",", ", ");
        this.unitNumber = Integer.valueOf(transportInfo[0]);
        this.identification = transportInfo[1];

        // Defines capacity based on streetcar type
        if (transportInfo[2].equals("S")) {
            this.capacity = 40;
        } else {
            this.capacity = 80;
        }
    }

}