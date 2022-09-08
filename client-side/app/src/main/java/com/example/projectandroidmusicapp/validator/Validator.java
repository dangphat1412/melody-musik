package com.example.projectandroidmusicapp.validator;

import com.example.projectandroidmusicapp.entity.User;

import java.util.List;

public class Validator {

    private final static String REGEX_USERNAME= "^[a-zA-Z ]{1,30}$";
    private final static String REGEX_EMAIL= "[A-Za-z]+[A-Za-z0-9]+@[A-Za-z0-9]+(.[A-Za-z0-9]+){1,3}";
    private final static String REGEX_PASSWORD= "^[A-Za-z0-9!@$>?&*^]{8,30}$";


    public static boolean isUsername(String username){
        return username.matches(REGEX_USERNAME);
    }

    public static boolean isEmail(String email){
        return email.matches(REGEX_EMAIL);
    }
    public static boolean isExistedEmail(String email, List<User> userList){
        for (User user: userList) {
            if(user.getEmail().equals(email)){
                return false;
            }
        }
        return true;
    }

    public static boolean isPassword(String password){
        return password.matches(REGEX_PASSWORD);
    }




}
