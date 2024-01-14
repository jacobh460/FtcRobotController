package org.firstinspires.ftc.teamcode.Manipulator;

public class Utils {

    public static double lerp(double x0, double y0, double x1, double y1, double x){
        return (y0 * (x1 - x) + y1 * (x - x0))/(x1 - x0);
    }


    public static double clamp(double a, double min, double max){
        return Math.min(Math.max(a, min), max);
    }

    //use this function when you are worried about floating point errors causing your input to not fall exactly within the period of acos
    public static double acos(double i){
        return Math.acos(Utils.clamp(i, -1.0, 1.0));
    }

    public static double shortest_angle(double start, double end){
        return ((((end - start) % (2 * Math.PI)) + (3 * Math.PI)) % (2 * Math.PI)) - Math.PI;
    }
}
