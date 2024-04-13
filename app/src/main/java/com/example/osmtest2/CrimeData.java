package com.example.osmtest2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CrimeData extends MapActivity {


    public CrimeData() {
        String filepath = "R.raw.thames_valley_street.csv";
        String searchTerm = "Reading";
    }
    public static String readRecord(String searchTerm,String filepath, String dataType, int filePos) {

        BufferedReader fileReader;
        String fileLine = null;

        try {
            fileReader = new BufferedReader(new FileReader(filepath));
            for(int i = 0; i < fileSize(); i++) {
                if (i == filePos) {
                    fileLine = fileReader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (dataType == "Longitude") {
            return fileLine;
        }
        else if (dataType == "Latitude") {
            return fileLine;
        }
        else if (dataType == "Crime") {
            return fileLine;
        }
        return searchTerm;
    }

    public static double returnLong(String searchTerm,String filepath, int filePos) {
        String long_str = readRecord(searchTerm, filepath, "Longitude", filePos);
        return 0;
    }
    public static double returnLat(String searchTerm,String filepath, int filePos) {
        String lat_str = readRecord(searchTerm, filepath, "Latitude", filePos);
        return 0;
    }
    public static String returnCrime(String searchTerm,String filepath, int filePos) {
        String crime_val = readRecord(searchTerm, filepath, "CrimeType", filePos);
        return crime_val;
    }

    public static int fileSize() {

        return 0;
    }
}

