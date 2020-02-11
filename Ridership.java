// Class holds the data about each rider described in the ridership.txt file
public class Ridership {

    // Initializes variables used in the class
    private String personID;
    private char ageGroup;
    private String transportMod;
    private int hour;
    private String date;
    private double capacityValue;

    // Constructor class takes an array of Strings that is organizes
    Ridership(String[] riderInfo) {
        this.personID = riderInfo[0];
        this.ageGroup = riderInfo[2].charAt(0);
        this.transportMod = riderInfo[1];
        this.hour = Integer.valueOf(riderInfo[3]);
        this.date = riderInfo[4];
        this.capacityValue = initializeAG(this.ageGroup);
    }

    // Method defines the rider's capacity value based on their age group
    private double initializeAG(char ageGroup) {
        switch (ageGroup) {
        case ('C'):
            return 0.75;
        case ('S'):
            return 1.25;
        default:
            return 1.0;
        }
    }

    // Accessor methods for the class's variables
    public String getPersonID() {
        return this.personID;
    }

    public char getAgeGroup() {
        return this.ageGroup;
    }

    public String getTransportMod() {
        return this.transportMod;
    }

    public int getHour() {
        return this.hour;
    }

    public String getDate() {
        return this.date;
    }

    public double getCapacityValue() {
        return this.capacityValue;
    }

    // Used in test cases to ensure the data is being read properly 
    public String printData() {
        String string = this.personID + ", " + this.ageGroup + ", " + this.transportMod + ", " + this.hour + ", "
                + this.date;
        return string;
    }
}