package com.example.osmtest2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CrimeData extends MapActivity {
    private static Scanner x;

    public CrimeData() {
        String filepath = "R.raw.thames_valley_street.csv";
        String searchTerm = "Reading";
        readRecord(filepath, searchTerm);
    }
    public static String readRecord(String searchTerm,String filepath) {
        boolean found = false;
        String ID = ""; String name1 = ""; String age = "";
        try {
            x = new Scanner(new File(filepath));
            x.useDelimiter("[,\n]");

            while(x.hasNext() && !found) {
                ID = x.next();
                name1 = x.next();
                age = x.next();

                if (ID.equals(searchTerm)) {
                    found = true;
                }
            }

            if (found) {
                return "ID " + ID + " name " + name1 + " age " + age;
            }
            else {
                return "Record not found";
            }
        }
        catch (Exception e) {
                return "error";
        }
    }
}

