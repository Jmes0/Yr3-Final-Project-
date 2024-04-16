package com.example.osmtest2;

import android.os.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class CrimeData extends MapActivity {


    public CrimeData() {
        String filepath = "R.raw.thames_valley_street.csv";
        String searchTerm = "Reading";
    }
    public static String readRecord(String csvdata, String crimeType, int filePos) {

        String result = null;
        int size = fileSize(csvdata);
        String[] separatedData = csvdata.split(",");
        int columns = 11;

        for(int j = 0; j < fileSize(csvdata); j++) {
            for(int i = 0; i < 11; i++) {
                if (i == filePos) {

                    //int line = i * columns;
                    result = separatedData[i];
                    //line++;
                }

            }
        }


        if (crimeType == "Longitude") {
            return result;
        }
        else if (crimeType == "Latitude") {
            return result;
        }
        else if (crimeType == "Crime") {
            return result;
        }
        return "Hellowww";
    }

    public static double returnLong(String filepath, String crimeType, int filePos) {
        String long_str = readRecord(filepath, crimeType, filePos);
        double long_db = Double.parseDouble(long_str);
        return long_db;
    }
    public static double returnLat(String filepath, String crimeType, int filePos) {
        String lat_str = readRecord(filepath, crimeType, filePos);
        double lat_db = Double.parseDouble(lat_str);
        return lat_db;
    }
    public static String returnCrime(String filepath, String crimeType, int filePos) {
        String crime_val = readRecord(filepath, crimeType, filePos);
        return crime_val;
    }

    public static int fileSize(String csvData) {
        String data = csvData;
        //int count = 0;
        String[] parts = data.split(",");
        int size = (parts.length) / 11;
    return Math.round(size) - 1;
    }
}

