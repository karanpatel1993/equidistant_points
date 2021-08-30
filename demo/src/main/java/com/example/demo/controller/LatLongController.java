package com.example.demo.controller;

import com.example.demo.helper.Coordinate;
import com.example.demo.helper.Points;
import com.example.demo.helper.PolyLine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/maps")
public class LatLongController {
    @GetMapping("/get-points")
    public HashMap<String, String> getDirections(@RequestParam("origin") String source, @RequestParam("destination") String destination) {
        // Set the interval distance in meters
        int INTERVAL_DISTANCE=50;

        // Fetch data from google directions api
        try{
            URL yahoo = new URL("https://maps.googleapis.com/maps/api/directions/json?" +
                   "origin=" + source + "&destination=" + destination + "&mode=driving" +
                   "&key=AIzaSyAEQvKUVouPDENLkQlCF6AAap1Ze-6zMos");

            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            StringBuilder data = new StringBuilder();

            while ((inputLine = in.readLine()) != null){
                data.append(inputLine);
            }
            in.close();

            // Get origin and destination points
            String [] origin_coords = source.split(",");
            Coordinate origin_point = new Coordinate(Double.parseDouble(origin_coords[0]), Double.parseDouble(origin_coords[1]));
            String [] dest_coords = destination.split(",");
            Coordinate destination_point = new Coordinate(Double.parseDouble(dest_coords[0]), Double.parseDouble(dest_coords[1]));

            // Convert String to json object
            JSONObject data_object  = new JSONObject(data.toString());

            // Extract the list of PolyLines
            JSONArray routes = data_object.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

            List<String> polyLines = new ArrayList<>();
            // Add all the polylines to the list
            for(int step = 0; step < steps.length(); step ++){
                String p = steps.getJSONObject(step).getJSONObject("polyline").getString("points");
                polyLines.add(p);
            }

            // Decode the polylines and add the coordinates to a list
            List<Coordinate> points = new ArrayList<>();
            // Add origin point to the list
            points.add(origin_point);

            // Add the coordinates from each decoded polyline
            for (String polyLine : polyLines) {
                List<Coordinate> segment_points = PolyLine.decode(polyLine);
                points.addAll(segment_points);
            }

            // Add destination point to the list
            points.add(destination_point);

            List<Coordinate> newPoints = Points.getNewPoints(points, INTERVAL_DISTANCE, origin_point, destination_point);

            List<String> pointsList = new ArrayList<>();
            HashMap<String, String> output = new HashMap<>();
            System.out.println("Output to plot the points");
            for (Coordinate o : newPoints) {
                pointsList.add(o.toString());
                System.out.println(o + ",#0000FF,marker,'Dummy'");
            }
            output.put("origin", origin_point.toString());
            output.put("destination", destination_point.toString());
            output.put("new_points", pointsList.toString());
            return output;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
