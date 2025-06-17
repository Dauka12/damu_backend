package com.AFM.AML.Course.models;

import java.io.*;

public class UniqueIntegerGenerator {
    private static final String FILE_PATH = "lastUniqueInteger.txt";
    private static int uniqueInteger;

    static {
        // Load the last generated integer from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                uniqueInteger = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public int getNextUniqueInteger() {
        // Increment the unique integer
        int nextUniqueInteger = ++uniqueInteger;

        // Save the updated value to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(String.valueOf(nextUniqueInteger));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nextUniqueInteger;
    }
}
