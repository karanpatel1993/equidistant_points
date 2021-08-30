package com.example.demo.controller;

import com.example.demo.helper.LatLng;
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
import java.util.List;

@RestController
@RequestMapping("/maps")
public class LatLongController {
    @GetMapping("/get-direction")
    public void getDirections(@RequestParam("origin") String source, @RequestParam("destination") String destination) {
        try{
            URL yahoo = new URL("https://maps.googleapis.com/maps/api/directions/json?" +
                   "origin=" + source + "&destination=" + destination +
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
            List<LatLng> points = new ArrayList<>();
            // Add origin point to the list
            LatLng origin = new LatLng(12.93175,77.62872);
            points.add(origin);

            // Add the coordinates from each decoded polyline
            for (String polyLine : polyLines) {
                List<LatLng> segment_points = PolyLine.decode(polyLine);
                // System.out.println("Segment Length:" + segment_points.size());
                points.addAll(segment_points);
            }
//            points.addAll(PolyLine.decode(polyLines.get(0)));
//            for (LatLng point : points) {
//                System.out.println(point);
//            }

            // Add destination point to the list
            LatLng destination_point = new LatLng(12.92662,77.63696);
            points.add(destination_point);
            System.out.println("Final Length:" + points.size());

            List<LatLng> output = Points.getNewPoints(points, 50);
            System.out.println("Output Size:" +output.size());
            for (LatLng o : output) {
                System.out.println(o + ",#0000FF,marker,Sample");
            }


        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return null
    }
}
