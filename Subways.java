// Child class of Vehicle that holds the information about every Subway defined in the subways.txt file
public class Subways extends Vehicle {

    // Initializes variables used in the class
    private int passPerCar;
    private char operationalStatus;
    private String operationalDate;

    // Other variables and accessor methods are inherited from the Vehicle class
    // Constructor class takes an array of Strings that is organizes
    Subways(String[] transportInfo, String description) {

        this.description = description.replace(",", ", ");
        this.unitNumber = Integer.valueOf(transportInfo[0]);
        this.identification = transportInfo[1];
        this.capacity = Integer.valueOf(transportInfo[2]) * Integer.valueOf(transportInfo[3]);
        this.operationalStatus = transportInfo[4].charAt(0);
        this.operationalDate = transportInfo[5];
    }

    // Checks to see if the Subway is operational on a given day
    public boolean isNotOperational(String date) {
        if (date.equals(this.operationalDate) && this.operationalStatus != 'A') {
            return true;
        } else {
            return false;
        }
    }

    // Accessor methods used to return variables
    public int getPPC() {
        return this.passPerCar;
    }

    public char getOS() {
        return this.operationalStatus;
    }

    public String getOD() {
        return this.operationalDate;
    }


}