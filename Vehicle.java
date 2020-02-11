// Parent Vehicle class used to define each child class
public class Vehicle {

    // Initializes variables used in the clas
    public String identification;
    public int unitNumber;
    public int capacity;
    public String description;

    // Accessor methods used to return variables
    public String getID() {
        return this.identification;
    }

    public int getUN() {
        return this.unitNumber;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public String getDescription() {
        return this.description;
    }

}