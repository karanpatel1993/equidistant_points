A Spring Boot Application to calculate 'real' points on the road that connect two given points using the google directions API. The points are present at a fixed interval. 

# End_points
1. latlong/get_interval_points
   
   METHOD: GET
   
   PARAMETERS: origin, destination
   
   SAMPLE: http://localhost:8080/latlong/get_interval_points?origin=19.14886497717458,72.93959337752814&destination=19.146625525670366,72.93375095066642

# Source code
## Controllers
1. LatLongController - <src/main/java/com/example/demo/controller/LatLongController.java>

The main driver class which calculates the equidistant points between the source and the destination

## Helper classes
1. Points - <src/main/java/com/example/demo/helper/Points.java>

The helper class which contains the core logic of the application.

2. PolyLine - <src/main/java/com/example/demo/helper/PolyLine.java>

The helper class to convert polyline code to their respective coordinates.

3. Coordinate - <src/main/java/com/example/demo/helper/Coordinate.java>

The helper class which represents an individual coordinate point on the map in terms of latitude and longitude.


