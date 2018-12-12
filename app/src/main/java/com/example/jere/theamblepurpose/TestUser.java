package com.example.jere.theamblepurpose;

public class TestUser {
    private static int totalPoints;
    private static double distanceTravelled;
    private static int routesComplete;
    private static String name;


    public TestUser(){
        name = "Test User";
        totalPoints = 0;
        distanceTravelled = 0;
        routesComplete = 0;
    }
    public static void addPoints(int addedPoints) {
        totalPoints = totalPoints + addedPoints;
    }

    public static int getTotalPoints() {
        return totalPoints;
    }

    public static void addDistance(double addedDistance) {
        distanceTravelled = distanceTravelled + addedDistance;
    }

    public static double getDistanceTravelled() {
        return distanceTravelled;
    }

    public static void addCompletedRoute() {
        routesComplete++;
    }

    public static int getCompleteRoutesAmount() {
        return routesComplete;
    }

    public static String getName() {
        return name;
    }
}
