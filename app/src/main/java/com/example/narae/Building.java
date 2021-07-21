package com.example.narae;

public class Building {
    private String buildingname;
    private double rightlatitude; //35.~~
    private double leftlatitude;
    private double uppderlongitude; //128.~~
    private double bottomlongitude;

    public Building(String na, double rila, double lela, double uplo, double bolo){
        buildingname = na;
        rightlatitude = rila;
        leftlatitude = lela;
        uppderlongitude = uplo;
        bottomlongitude = bolo;
    }
    public String getName(){return buildingname;}
    public double getRightlatitude(){return rightlatitude;}
    public double getLeftlatitude(){return leftlatitude;}
    public double getUppderlongitude(){return uppderlongitude;}
    public double getBottomlongitude(){return bottomlongitude;}

}
