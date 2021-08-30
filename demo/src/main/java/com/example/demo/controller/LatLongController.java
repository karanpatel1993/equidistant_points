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
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/maps")
public class LatLongController {
    @GetMapping("/get-points")
    public HashMap<String, List<String>> getDirections(@RequestParam("origin") String source, @RequestParam("destination") String destination) {
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
            String data = "";

            while ((inputLine = in.readLine()) != null){
                data += inputLine;
                // System.out.println(inputLine);
            }
            in.close();
            System.out.println(data);

            // Get origin and destination points
            String [] origin_coords = source.split(",");
            Coordinate origin_point = new Coordinate(Double.parseDouble(origin_coords[0]), Double.parseDouble(origin_coords[1]));
            String [] dest_coords = source.split(",");
            Coordinate destination_point = new Coordinate(Double.parseDouble(dest_coords[0]), Double.parseDouble(dest_coords[1]));

            // Convert String to json object
            JSONObject data_object  = new JSONObject(data);

            // Extract the list of PolyLines
            JSONArray routes = data_object.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

            List<String> polyLines = new ArrayList<>();
            // Add all the polylines to the list
            for(int step = 0; step < steps.length(); step ++){
                String p = steps.getJSONObject(step).getJSONObject("polyline").getString("points");
                System.out.println(p);
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
            System.out.println("Final Length:" + points.size());

            List<Coordinate> newPoints = Points.getNewPoints(points, INTERVAL_DISTANCE, origin_point, destination_point);
            System.out.println("Output Size:" +newPoints.size());
            for (Coordinate o : newPoints) {
                System.out.println(o + ",#0000FF,marker,Sample");
            }

            List<String> p = new ArrayList<>();
            HashMap<String, List<String>> output = new HashMap<>();
            for (Coordinate o : newPoints) {
                p.add(o.toString());
                //System.out.println(o + ",#0000FF,marker,Sample");
            }
            System.out.println(p.size());
            output.put("new_points", p);
            return output;

        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
