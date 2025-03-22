package com.example.thecupcakeshop.model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class InputValidations {
    public static String timeHandler() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String dateHandler() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String productHandler(int user_type) {
        int id = (int) (Math.random() * 10000);
        String productID;

        switch (user_type) {
            case 1:
                productID = "REC-" + id + "";
                break;
            case 2:
                productID = "DCID-" + id + "";
                break;
            case 3:
                productID = "PHID-" + id + "";
                break;
            case 4:
                productID = "" + id + "";
                break;
            default:
                productID = "UNK-" + id + "";
                break;
        }
        return productID;
    }

    public static final String passwordHandler() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String num = "0123456789";
        String specialChar = "!@#%";
        String combination = upper + upper.toLowerCase() + num + specialChar;
        int len = 10;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(combination.charAt(
                    ThreadLocalRandom.current().nextInt(
                            combination.length()
                    )
            ));
        }
        return sb.toString();
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^(.+)@(\\S+)$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }

    public static boolean isDouble(String text) {
        return text.matches("[0-9]{1,13}(\\.[0-9]*)?");
    }

    public static boolean isInteger(String text) {
        return text.matches("^\\d+$");
    }

    public static boolean isMobileValied(String mobile) {
        return mobile.matches("^07[01245678]{1}[0-9]{7}$");
    }


}
