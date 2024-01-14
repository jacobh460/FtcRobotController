package org.firstinspires.ftc.teamcode.Manipulator.Spline;

import org.firstinspires.ftc.teamcode.Manipulator.Vector2;

public class Point {
    public Vector2 pos;
    public double t;
    public double L1;
    public double thetaB;
    public Point(double x, double y, double t, double L1, double thetaB){
        this(new Vector2(x, y), t, L1, thetaB);
    }

    public Point(Vector2 pos, double t, double L1, double thetaB){
        this.pos = pos;
        this.t = t;
        this.L1 = L1;
        this.thetaB = thetaB;

    }

    public boolean mustInterpolate(){
        //you must interpolate one of these values if they are not NaN
        return this.L1 == this.L1 || this.thetaB == this.thetaB;
    }


}
