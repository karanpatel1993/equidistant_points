package com.example.demo.helper;

import java.util.ArrayList;
import java.util.List;

public class Points {
    /**
     * This method is used to calculate the new Points placed at a distance of the interval.
     * @param points This is the list of points from Directions API.
     * @param interval  This is the interval at which the new points need to placed.
     * @param origin The start point coordinate.
     * @param destination The end point coordinate.
     * @return List<Coordinate> The list of new points.
     */
    public static List<Coordinate> getNewPoints(List<Coordinate> points, double interval, Coordinate origin, Coordinate destination){
        List <Coordinate> result = new ArrayList<>();
        // LatLng origin = new LatLng(12.93175,77.62872);
        result.add(origin);

        Coordinate prevPoint = points.get(0);
        double currentIntervalDistance = 0;
        // Loop through the points
        for(int i = 1; i < points.size(); i++){
            Coordinate currentPoint = points.get(i);
            double adjacentPointDistance = calculate_distance(prevPoint, currentPoint);
            if (currentIntervalDistance + adjacentPointDistance > interval){
                double offsetDistance = interval - currentIntervalDistance;
                double bearing = calculate_bearing(prevPoint, currentPoint);
                Coordinate newPoint = new_coordinates(prevPoint, bearing, offsetDistance);
                result.add(newPoint);
                currentIntervalDistance = 0;
                prevPoint = newPoint;
            }
            else{
                currentIntervalDistance += adjacentPointDistance;
                prevPoint = currentPoint;
            }
        }
        result.add(destination);
        return result;
    }

    /**
     * This method is used to calculate the distance between two coordinate points.
     * @param src The start point coordinate.
     * @param des  The end point coordinate.
     * @return double The distance between the points in meters.
     */
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

    /**
     * This method is used to calculate the bearing between the points.
     * @param src The start point coordinate.
     * @param dst  The end point coordinate.
     * @return double The bearing between the points in radians
     */
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

    /**
     * This method is used to calculate the new coordinate point given the source and the distance and the bearing.
     * @param src The start point coordinate.
     * @param bearing The bearing/angle in radians
     * @param distance The distance between the source and the new point.
     * @return Coordinate The new coordinate point.
     */
    public static Coordinate new_coordinates(Coordinate src, double bearing, double distance){
        final double R = 6378100; // Radius of the earth
        double srcLat = Math.toRadians(src.getLatitude());
        double srcLon = Math.toRadians(src.getLongitude());
        double dstLat = Math.asin(Math.sin(srcLat) * Math.cos(distance/R) +
                Math.cos(srcLat) * Math.sin(distance/R) * Math.cos(bearing));

        double dstLon = srcLon + Math.atan2(Math.sin(bearing) * Math.sin(distance / R) * Math.cos(srcLat),
                Math.cos(distance/R) - Math.sin(srcLat) * Math.sin(dstLat));

        dstLat = Math.toDegrees(dstLat);
        dstLon = Math.toDegrees(dstLon);

        return new Coordinate(dstLat, dstLon);

    }
}
