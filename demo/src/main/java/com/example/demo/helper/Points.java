package com.example.demo.helper;

import java.util.ArrayList;
import java.util.List;

public class Points {
    public static List<Coordinate> getNewPoints(List<Coordinate> points, double interval, Coordinate origin, Coordinate destination){
        List <Coordinate> result = new ArrayList<>();
        // LatLng origin = new LatLng(12.93175,77.62872);
        result.add(origin);

        Coordinate prevPoint = points.get(0);
        double currentIntervalDistance = 0;
        // Loop through the points
        for(int i = 1; i < points.size(); i++){
            System.out.println("Loop:"+ i);
            Coordinate currentPoint = points.get(i);
            System.out.println("Prev Point:"+ prevPoint);
            System.out.println("Current Point:"+ currentPoint);
            double adjacentPointDistance = calculate_distance(prevPoint, currentPoint);
            System.out.println("Adjacent Distance:"+ adjacentPointDistance);
            System.out.println("Current Interval Distance:"+ currentIntervalDistance);
            if (currentIntervalDistance + adjacentPointDistance > interval){
                System.out.println("If:");
                double offsetDistance = interval - currentIntervalDistance;
                System.out.println("OffestDistance:" + offsetDistance);
                double bearing = calculate_bearing(prevPoint, currentPoint);
                System.out.println("Bearing:" + Math.toDegrees(bearing));
                Coordinate newPoint = new_coordinates(prevPoint, bearing, offsetDistance);
                System.out.println("newPoint:" + newPoint);
                result.add(newPoint);
                currentIntervalDistance = 0;
                prevPoint = newPoint;
            }
            else{
                System.out.println("Else");
                currentIntervalDistance += adjacentPointDistance;
                prevPoint = currentPoint;
            }
        }
        result.add(destination);
        return result;
    }

    public static double calculate_distance(Coordinate src, Coordinate des) {
        final int R = 6378; // Radius of the earth
        double src_lat = Math.toRadians(src.getLatitude());
        double src_lon = Math.toRadians(src.getLongitude());
        double des_lat = Math.toRadians(des.getLatitude());
        double des_lon = Math.toRadians(des.getLongitude());
        double latDistance = des_lat - src_lat;
        double lonDistance = des_lon - src_lon;
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(src_lat) * Math.cos(des_lat)
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }

    public static double calculate_bearing(Coordinate src, Coordinate dst){
        double srcLat = Math.toRadians(src.getLatitude());
        double dstLat = Math.toRadians(dst.getLatitude());
        double srcLon = Math.toRadians(src.getLongitude());
        double dstLon = Math.toRadians(dst.getLongitude());
        double dLng = dstLon - srcLon;

        return Math.atan2(Math.sin(dLng) * Math.cos(dstLat),
                Math.cos(srcLat) * Math.sin(dstLat) -
                        Math.sin(srcLat) * Math.cos(dstLat) * Math.cos(dLng));
    }

    public static Coordinate new_coordinates(Coordinate src, double bearing, double distance){
        System.out.println("New Coordinates Input Parameters");
        System.out.println("Input:" + src + ',' + distance + ',' + bearing);
        final double R = 6378100; // Radius of the earth
        double srcLat = Math.toRadians(src.getLatitude());
        double srcLon = Math.toRadians(src.getLongitude());
        System.out.println("src Lat:" + srcLat);
        System.out.println("src Long:" + srcLon);
        System.out.println("Math.sin(srcLat):" + Math.sin(srcLat));
        System.out.println("Math.cos(distance/R):" + Math.cos(distance/R));
        System.out.println("Math.cos(srcLat):" + Math.cos(srcLat));
        System.out.println("Division:" + distance/R);
        System.out.println("Math.sin(distance/R):" + Math.sin(distance/R));
        System.out.println("Bearing:" + bearing);
        System.out.println("Math.cos(bearing):" + Math.cos(bearing));
        double dstLat = Math.asin(Math.sin(srcLat) * Math.cos(distance/R) +
                Math.cos(srcLat) * Math.sin(distance/R) * Math.cos(bearing));

        double dstLon = srcLon + Math.atan2(Math.sin(bearing) * Math.sin(distance / R) * Math.cos(srcLat),
                Math.cos(distance/R) - Math.sin(srcLat) * Math.sin(dstLat));
        System.out.println("Radians Des Lat:" + dstLat);
        System.out.println("Radians Des Long:" + dstLon);

        dstLat = Math.toDegrees(dstLat);
        dstLon = Math.toDegrees(dstLon);
        System.out.println("Degrees Des Lat:" + dstLat);
        System.out.println("Degrees Des Long:" + dstLon);

        return new Coordinate(dstLat, dstLon);

    }
}
