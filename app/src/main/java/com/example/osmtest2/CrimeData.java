package com.example.osmtest2;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class CrimeData {
    public static double returnCoordinates(String data, String type) {
        String[] lineArray = data.split("\\s*,\\s*");
        String cd = "";
        double cd_db;
        if (type.equals("Longitude")) {
            cd = lineArray[4];
        } else if (type.equals("Latitude")) {
            cd = lineArray[5];
        }

        if(isDouble(cd)) {
            cd_db = Double.parseDouble(cd);
        }
        else {
            return 0;
        }

        return cd_db;
    }

    static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String returnCrime(String data) {
        String[] lineArray = data.split("\\s*,\\s*");
        String cd = lineArray[9];
        return cd;

    }

    public static String returnCity(String data, String city) {
        String[] lineArray = data.split("\\s*");
        String cd = "";
        cd = lineArray[8];
        String[] cdArray = data.split("\\s+");
        for(String a : cdArray) {
            if(a.equals(city)) {
                return a;
            }
        }
        return null;
    }
}