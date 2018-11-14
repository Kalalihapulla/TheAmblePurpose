package com.example.jere.theamblepurpose;

public class Route {
    private static String testString;

   public Route(String test) {
       testString = test;
   }

   public static String getString(){
       return testString;
   }
}
