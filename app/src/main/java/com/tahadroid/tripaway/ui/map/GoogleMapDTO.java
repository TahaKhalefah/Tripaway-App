package com.tahadroid.tripaway.ui.map;

import java.util.ArrayList;

class GoogleMapDTO {

    ArrayList<Routes> routes = new ArrayList<Routes>();
}

class Routes {
    ArrayList<Legs> legs = new ArrayList<Legs>();
}

class Legs {
    Distance distance = new Distance();
    Duration duration = new Duration();
    String end_address = "";
    String start_address = "";
    Location end_location = new Location();
    Location start_location = new Location();
    ArrayList<Steps> steps = new ArrayList<Steps>();
}

class Steps {
    Distance distance = new Distance();
    Duration duration = new Duration();
    String end_address = "";
    String start_address = "";
    Location end_location = new Location();
    Location start_location = new Location();
    PolyLine polyline = new PolyLine();
    String travel_mode = "";
    String maneuver = "";
}

class Duration {
    String text = "";
    int value = 0;
}

class Distance {
    String text = "";
    int value = 0;
}

class PolyLine {
    String points = "";
}

class Location {
    String lat = "";
    String lng = "";
}