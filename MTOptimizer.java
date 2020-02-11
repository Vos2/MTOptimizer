
/*
Name: Joshua Neizer
Student ID: 20104131
NET ID: 17JAN3
Program Description: 
        A batch program that runs through the data about a city's public transport 
        habits and develops and optimal amount of vehicles that should be used to 
        reduce the amount of vehicles used at a given time per vehicle type.
*/

// Imports java libraries for the program
import java.io.*;
import java.util.*;

// Main driving class
public class MTOptimizer {

    //Initializes the ArrayLists used in the program that hold all the classes 
    private ArrayList<Ridership> RidershipData = new ArrayList<Ridership>();

    private ArrayList<Buses> Bus = new ArrayList<Buses>();
    private ArrayList<GoBuses> GoBus = new ArrayList<GoBuses>();
    private ArrayList<GoTrains> GoTrain = new ArrayList<GoTrains>();
    private ArrayList<Streetcars> Streetcar = new ArrayList<Streetcars>();
    private ArrayList<Subways> Subway = new ArrayList<Subways>();

    // Contains data about the amount of riders for each vehicle at every hour
    private double[][] capacityCount = new double[24][5];

    // Initializes PrintWriter variables to write to 
    private PrintWriter errorlog;
    private PrintWriter InOperationFleets;

    // Constructor class to drive the class
    MTOptimizer() {

        // Reads in the data for every vehicle and places them in their respective ArrayList
        readInVehicleData("buses.txt");
        readInVehicleData("gobuses.txt");
        readInVehicleData("gotrains.txt");
        readInVehicleData("streetcars.txt");
        readInVehicleData("subways.txt");

        // Reads in the Ridership data
        readInRidershipData();

        // Writes the InOperationFleets text file
        writeIOF();

        checkRDSort();

        
        for (int i = 0; i < 24; i++){
            for (int j = 0; j < 5; j++){
                System.out.println(capacityCount [i] [j]);
            }
            System.out.println();
        }
        

    }

    private void readInVehicleData(String textFile) {
        // Initializes the variables used in the method
        BufferedReader br;
        String st;

        // Opens the desired text file if it exists, and if it doesn't, it creates it
        // Checks for file I/O exceptions
        try {
            br = new BufferedReader(new FileReader(new File(textFile)));

            // Depending the text file, the file's vehicles are instantiated to a class
            // As well as added their ArrayList

            // The files are read line by line until there are no lines left
            switch (textFile) {
            case ("buses.txt"):
                while ((st = br.readLine()) != null) {
                    this.Bus.add(new Buses(st.split(","), st));
                }
                break;
            case ("gobuses.txt"):
                while ((st = br.readLine()) != null) {
                    this.GoBus.add(new GoBuses(st.split(","), st));
                }
                break;
            case ("gotrains.txt"):
                while ((st = br.readLine()) != null) {
                    this.GoTrain.add(new GoTrains(st.split(","), st));
                }
                break;
            case ("streetcars.txt"):
                while ((st = br.readLine()) != null) {
                    this.Streetcar.add(new Streetcars(st.split(","), st));
                }
                break;
            case ("subways.txt"):
                while ((st = br.readLine()) != null) {
                    this.Subway.add(new Subways(st.split(","), st));
                    // If on the date "20190304" the vehicle is not operational, it is removed from the ArrayList
                    if (this.Subway.get(this.Subway.size() - 1).isNotOperational("20190304")) {
                        this.Subway.remove(this.Subway.size() - 1);
                    }
                }
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening the text file, program aborted!");
            System.exit(0);
        }
    }

    private void readInRidershipData() {
        // Initializes the variables used in the method
        BufferedReader br;
        String st;
        String[] currentData;
        int lineNumber = 0;

        // Opens the ridership.txt file if it exists
        // Checks for file I/O exceptions
        try {
            br = new BufferedReader(new FileReader(new File("ridership.txt")));

            // Opens the errorlog.txt file if it exists, and if it doesn't, it creates it
            // Checks for file I/O exceptions
            try {
                errorlog = new PrintWriter(new FileWriter("errorlog.txt"));
            } catch (IOException e) {
                // Ensures there is no error when writing to the errorlog.txt
                e.printStackTrace();
                System.out.println("Error opening the file 'errorlog.txt'. File aborted!");
                System.exit(0);
            }

            // Reads the rest of the file until there is no new line
            while ((st = br.readLine()) != null) {
                // The current data is read is as an array of Strings, error checked and cloned the array to avoid aliasing 
                currentData = checkData(st.split(","), lineNumber).clone();

                // If the line has no errors then the rider is added to the RidershipData ArrayList
                if (currentData.length > 1) {
                    this.RidershipData.add(new Ridership(currentData));
                    int currentRider = RidershipData.size() - 1;

                    // The current rider is also checked to see what vehicle they are using and when 
                    capacityPHPV(RidershipData.get(currentRider).getHour() - 1,
                            RidershipData.get(currentRider).getTransportMod(),
                            RidershipData.get(currentRider).getCapacityValue());
                }
                
                // Line Number is kept track of for the errorlog 
                lineNumber += 1;
            }

            // Collections.sort(this.RidershipData,
            // (TimeA, TimeB) ->
            // TimeA.getTransportMod().compareTo(TimeB.getTransportMod()));

            // Closes the errorlog so it can be written to 
            errorlog.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening the text file, program aborted!");
            System.exit(0);
        }
    }

    // Method checks the ridership data for errors and reports them 
    private String[] checkData(String[] data, int lineNumber) {

        // Known Age Groups (AG) and Transport Modality (TM) addressed in the ridership file
        String[] AG = new String[] { "C", "A", "S" };
        String[] TM = new String[] { "S", "G", "X", "C", "D" };

        // For every error, the type of error and line is printed in the error log file

        // Checks data to see if it has the right number of inputs
        if (data.length == 5) {
            // Checks the Person Identification for errors 
            if (data[0].equals("*") || data[0].length() == 7 && checkInt(data [0]) || data[0].length() == 
                16 && checkInt(data[0].substring(1, data[0].length()))) {
                // Ensures that the Age Group is correct 
                if (Arrays.asList(AG).contains(data[2])) {
                    // Ensures that the Transport Modality is correct
                    if (Arrays.asList(TM).contains(data[1])) {
                            // Ensures that the time is in the correct range
                            if (checkInt(data[3]) && Integer.valueOf(data[3]) > -1 && 
                                Integer.valueOf(data[3]) < 25) {
                                if (data[4].length() == 8 && checkInt(data[4])) {
                                // If everything is correct than it is then the array is returned
                                return data;
                                } else {
                                    errorlog.print("ERROR on Line " + lineNumber + ": " + Arrays.toString(data)
                                            + "\r\nInvalid Date\r\n\r\n");
                                }
                            } else {
                                errorlog.print("ERROR on Line " + lineNumber + ": " + Arrays.toString(data)
                                        + "\r\nInvalid Time\r\n\r\n");
                            }
                    } else {
                        errorlog.print("ERROR on Line " + lineNumber + ": " + Arrays.toString(data)
                                + "\r\nInvalid Transport Modality\r\n\r\n");
                    }
                } else {
                    errorlog.print("ERROR on Line " + lineNumber + ": " + Arrays.toString(data)
                            + "\r\nInvalid Age Group\r\n\r\n");
                }
            } else {
                errorlog.print("ERROR on Line " + lineNumber + ": " + Arrays.toString(data)
                        + "\r\nInvalid Person Identification\r\n\r\n");
            }
        } else {
            errorlog.print("ERROR on Line " + lineNumber + ": " + Arrays.toString(data)
                    + "\r\nInvalid Number of Inputs\r\n\r\n");
        }

        // If there is an error then an empty array is returned
        return new String[] {};
    }

    // Method returns a boolean to whether to whether or not a variable is a number 
    private boolean checkInt(String currentInt){
        try {
            Long.valueOf(currentInt);
            return true;

        } catch (NumberFormatException e) {
           return false;
        }

    }

    // GET RID OF THIS!!!
    private void checkRDSort() {
        PrintWriter test;

        try {
            test = new PrintWriter(new FileWriter("test.txt"));

            for (int x = 0; x < Bus.size(); x++) {
                test.print(Bus.get(x).getDescription() + "\r\n");
            }

            test.close();
        } catch (IOException e) {
            // Ensures there is no error when writing to the errorlog.txt
            e.printStackTrace();
            System.out.println("Error opening the file 'errorlog.txt'. File aborted!");
            System.exit(0);
        }
    }

    // Method writes to the InOperationFleets text file, as outlined in the assignment
    private void writeIOF() {

        // Each vehicle ArrayList is sorted using a stream and Lambda function based on capacity
        Collections.sort(this.Bus, (CapA, CapB) -> CapA.getCapacity() - CapB.getCapacity());
        Collections.sort(this.GoBus, (CapA, CapB) -> CapA.getCapacity() - CapB.getCapacity());
        Collections.sort(this.GoTrain, (CapA, CapB) -> CapA.getCapacity() - CapB.getCapacity());
        Collections.sort(this.Subway, (CapA, CapB) -> CapA.getCapacity() - CapB.getCapacity());
        Collections.sort(this.Streetcar, (CapA, CapB) -> CapA.getCapacity() - CapB.getCapacity());

        // GET RID OF THIS!!!!!
        checkRDSort();

        // Opens the InOperationFleets.txt file if it exists, and if it doesn't, it creates it
        // Checks for file I/O exceptions
        try {
            InOperationFleets = new PrintWriter(new FileWriter("InOperationFleets.txt"));

        } catch (IOException e) {
            // Ensures there is no error when writing to the InOperationFleets.txt
            e.printStackTrace();
            System.out.println("Error opening the file 'InOperationFleets.txt'. File aborted!");
            System.exit(0);
        }

        // For each vehicle, the optimal amount of vehicles per every hour is written to the text file
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 24; j++) {
                switch (i) {
                
                // When a the start of each vehicle, a header is written
                // Add the start of every hour, a sub-header with the hour
                // The printResults method will take the results form the optimizedVehicleCount method... 
                // and vehicle type to print the result

                case (0):
                    if (j == 0) {
                        InOperationFleets.print("[Buses]\r\n");
                    }
                    InOperationFleets.print(repeat(" ", 4) + "[Hour = " + (int) (j + 1) + "]\r\n");

                    printResults(optimizedVehicles(Bus, capacityCount[j][i]), Bus);

                    break;
                case (1):
                    if (j == 0) {
                        InOperationFleets.print("[GoBuses]\r\n");
                    }
                    InOperationFleets.print(repeat(" ", 4) + "[Hour = " + (int) (j + 1) + "]\r\n");

                    printResults(optimizedVehicles(GoBus, capacityCount[j][i]), GoBus);

                    break;
                case (2):
                    if (j == 0) {
                        InOperationFleets.print("[GoTrains]\r\n");
                    }
                    InOperationFleets.print(repeat(" ", 4) + "[Hour = " + (int) (j + 1) + "]\r\n");

                    printResults(optimizedVehicles(GoTrain, capacityCount[j][i]), GoTrain);

                    break;
                case (3):
                    if (j == 0) {
                        InOperationFleets.print("[Subway]\r\n");
                    }
                    InOperationFleets.print(repeat(" ", 4) + "[Hour = " + (int) (j + 1) + "]\r\n");

                    printResults(optimizedVehicles(Subway, capacityCount[j][i]), Subway);

                    break;
                case (4):
                    if (j == 0) {
                        InOperationFleets.print("[Streetcars]\r\n");
                    }
                    InOperationFleets.print(repeat(" ", 4) + "[Hour = " + (int) (j + 1) + "]\r\n");

                    printResults(optimizedVehicles(Streetcar, capacityCount[j][i]), Streetcar);

                    break;
                }
            }
        }

        InOperationFleets.print("\r\n");

        // Closes the text file to be written to 
        InOperationFleets.close();
    }
    // Method prints the optimal vehicle configuration for a vehicle type at a specific hour
    private void printResults(ArrayList<Integer> currentResults, ArrayList<?> VehicleUsed) {

        // The amount of vehicles used is printed out 
        InOperationFleets.print(repeat(" ", 8) + "[Count = " + currentResults.size() + "]\r\n");

        
        for (int vehicleIndex : currentResults) {
            // The generic ArrayList is used, so that the parent Vehicle class, can be used to access any of its specific children classes
            InOperationFleets.print(repeat(" ", 12) + Vehicle.class.cast(VehicleUsed.get(vehicleIndex)).getDescription() + "\r\n");
        }
        InOperationFleets.print("\r\n");
    }

    // Method keeps track of the amount of riders for a given vehicle at a specific hour
    private void capacityPHPV(int CCIndex, String vehicleType, double capacityValue) {
        // The CCIndex reflects the hour, while the vehicleType reflects the which vehicle the rider is using  
        switch (vehicleType) {
        case ("C"):
            this.capacityCount[CCIndex][0] += capacityValue;
            break;
        case ("D"):
            this.capacityCount[CCIndex][1] += capacityValue;
            break;
        case ("G"):
            this.capacityCount[CCIndex][2] += capacityValue;
            break;
        case ("S"):
            this.capacityCount[CCIndex][3] += capacityValue;
            break;
        case ("X"):
            this.capacityCount[CCIndex][4] += capacityValue;
            break;
        }
    }

    // Method will optimize the number vehicles of used at a given hour per vehicle type
    private ArrayList<Integer> optimizedVehicles(ArrayList<?> VehicleUsed, double capacity) {
        ArrayList<Integer> optimizedVehicleCount = new ArrayList<Integer>();
        int vehicleCount = VehicleUsed.size();
        double vehicleCC = 0.0;

        // The loop will run until no more vehicles are needed or all vehicles are being used 
        while (vehicleCount > 0 && vehicleCC < capacity) {
            for (int x = 0; x < vehicleCount; x++) {
                // Finds the vehicle that will have the closest capacity to what is needed
                // Again using the Vehicle parent class, any child class can be accessed 
                if (vehicleCC + Vehicle.class.cast(VehicleUsed.get(x)).getCapacity() >= capacity) {
                    optimizedVehicleCount.add(x);

                    return optimizedVehicleCount;
                }
            }

            // If the capacity needed is greater than the capacity of the biggest vehicle, then that vehicle is added 
            // Everything is repeated except the capacity needed is subtracted and that vehicle is ignored  
            vehicleCount -= 1;
            
            if (Vehicle.class.cast(VehicleUsed.get(vehicleCount)).getCapacity() != 0) {
                optimizedVehicleCount.add(vehicleCount);
            }
            vehicleCC += Vehicle.class.cast(VehicleUsed.get(vehicleCount)).getCapacity();
        }

        // An ArrayList of integers is returned that have the indexes of the vehicles that will be used  
        return optimizedVehicleCount;
    }

    // Method returns a repeated character, this is done for file formatting 
    private String repeat (String word, int amount){
        String temp = "";   
        
        for (int x=0; x < amount; x++){
            temp += word;
        }

        return temp;
    }

    // The Main is called and ran
    public static void main(String[] args) {
        new MTOptimizer();
    }
}