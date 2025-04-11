/**
 * Author: Lokesh Selvaraj
 * Description: Call Taxi Booking Application
 * This program simulates a real-world taxi booking system where taxis are assigned based on distance, time, and availability.
 * I did this project in Skillrack platform as a mini project. URL Link - "www.skillrack.com/cert/529380/DGW"
 */

import java.util.*;
import java.text.*;

class Taxi {
    int revenue;
    boolean isAvailable;
    int taxiId;
    int currentLocation;
    String nextAvailableTime;

    public Taxi(int taxiId) {
        this.taxiId = taxiId;
        this.currentLocation = 1;
        this.revenue = 0;
        this.nextAvailableTime = "00:00";
        this.isAvailable = true;
    }
}

public class TaxiBookingApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Read the number of taxis and points
        int numberOfTaxis = sc.nextInt();
        int numberOfPoints = sc.nextInt();

        // Distance between points
        int[] distances = new int[numberOfPoints - 1];
        for (int i = 0; i < numberOfPoints - 1; i++) {
            distances[i] = sc.nextInt();
        }

        // Travel times between points
        int[] travelTimes = new int[numberOfPoints - 1];
        for (int i = 0; i < numberOfPoints - 1; i++) {
            travelTimes[i] = sc.nextInt();
        }

        // Fare details
        int baseFare = sc.nextInt(); // base fare
        int baseDistance = sc.nextInt(); // base distance
        int farePerKm = sc.nextInt(); // fare per km after base distance
        int maxPickupDistance = sc.nextInt(); // max distance taxi can travel for pickup
        int numberOfBookings = sc.nextInt(); // total number of bookings

        // Create taxi objects
        List<Taxi> taxiList = new ArrayList<>();
        for (int i = 1; i <= numberOfTaxis; i++) {
            taxiList.add(new Taxi(i));
        }

        // Process each booking
        for (int i = 0; i < numberOfBookings; i++) {
            String customerId = sc.next();
            int pickupPoint = sc.nextInt();
            int dropPoint = sc.nextInt();
            String bookingTime = sc.next();

            // Update taxi availability based on current time
            for (Taxi taxi : taxiList) {
                if (!taxi.isAvailable && compareTime(taxi.nextAvailableTime, bookingTime) <= 0) {
                    taxi.isAvailable = true;
                }
            }

            Taxi assignedTaxi = null;
            int minPickupDistance = Integer.MAX_VALUE;

            // Assign nearest available taxi
            for (Taxi taxi : taxiList) {
                if (taxi.isAvailable && compareTime(taxi.nextAvailableTime, bookingTime) <= 0) {
                    int distanceToPickup = calculateDistance(taxi.currentLocation, pickupPoint, distances);
                    if (distanceToPickup <= maxPickupDistance) {
                        if (assignedTaxi == null || distanceToPickup < minPickupDistance ||
                            (distanceToPickup == minPickupDistance && taxi.revenue < assignedTaxi.revenue) ||
                            (distanceToPickup == minPickupDistance && taxi.revenue == assignedTaxi.revenue && taxi.taxiId < assignedTaxi.taxiId)) {
                            assignedTaxi = taxi;
                            minPickupDistance = distanceToPickup;
                        }
                    }
                }
            }

            if (assignedTaxi == null) {
                System.out.println(customerId + " REJECTED");
            } else {
                int tripDistance = calculateDistance(pickupPoint, dropPoint, distances);
                int fare = calculateFare(tripDistance, baseFare, baseDistance, farePerKm);
                String dropTime = calculateDropTime(pickupPoint, dropPoint, assignedTaxi.currentLocation, travelTimes, bookingTime);

                // Print booking confirmation
                System.out.println(customerId + " Taxi-" + assignedTaxi.taxiId + " " + fare + " " + dropTime);

                // Update taxi state
                assignedTaxi.revenue += fare;
                assignedTaxi.currentLocation = dropPoint;
                assignedTaxi.nextAvailableTime = dropTime;
                assignedTaxi.isAvailable = false;
            }
        }

        sc.close();
    }

    // Calculate fare based on base fare, base distance and fare/km
    public static int calculateFare(int distance, int baseFare, int baseDistance, int perKmFare) {
        if (distance <= baseDistance) {
            return baseFare;
        } else {
            return baseFare + (distance - baseDistance) * perKmFare;
        }
    }

    // Calculate distance between two points
    public static int calculateDistance(int pointA, int pointB, int[] distances) {
        int distance = 0;
        for (int i = Math.min(pointA, pointB); i < Math.max(pointA, pointB); i++) {
            distance += distances[i - 1];
        }
        return distance;
    }

    // Compare two time strings ("HH:mm")
    public static int compareTime(String time1, String time2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date d1 = sdf.parse(time1);
            Date d2 = sdf.parse(time2);
            return d1.compareTo(d2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Calculate the time the taxi will become free after the ride
    public static String calculateDropTime(int pickup, int drop, int currentLoc, int[] travelTimes, String bookingTime) {
        try {
            int totalTime = 0;

            // Time to reach pickup point from current location
            for (int i = Math.min(currentLoc, pickup); i < Math.max(currentLoc, pickup); i++) {
                totalTime += travelTimes[i - 1];
            }

            // Time to drop point from pickup
            for (int i = Math.min(pickup, drop); i < Math.max(pickup, drop); i++) {
                totalTime += travelTimes[i - 1];
            }

            // Calculate new time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date time = sdf.parse(bookingTime);
            long totalMinutes = (time.getTime() / 60000) + totalTime;
            int newHours = (int) (totalMinutes / 60) % 24;
            int newMinutes = (int) (totalMinutes % 60);

            return String.format("%02d:%02d", newHours, newMinutes);
        } catch (ParseException e) {
            e.printStackTrace();
            return bookingTime;
        }
    }
}
