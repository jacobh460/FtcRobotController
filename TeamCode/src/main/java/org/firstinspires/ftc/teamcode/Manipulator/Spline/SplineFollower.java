package org.firstinspires.ftc.teamcode.Manipulator.Spline;

import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorConfiguration;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;

public class SplineFollower {

    private ManipulatorController controller;

    private ParametricNaturalCubicSpline currentSpline = null;
    private boolean reversed = false;
    private long startTime = 0;

    private double max_accel = 0;

    private double max_vel = 0;
    public SplineFollower(ManipulatorController controller){
        this.controller = controller;
    }

    private double getTime(){
        return ((double)(System.nanoTime() - this.startTime)) * 1e-9;
    }

    public void followSpline(ParametricNaturalCubicSpline spline, boolean reversed, double max_accel, double max_vel){
        this.currentSpline = spline;
        this.reversed = reversed;
        this.startTime = System.nanoTime();
        this.max_accel = max_accel;
        this.max_vel = max_vel;
    }

    private double trapezoidalMotionProfile(double realTime){
        double max_t = this.currentSpline.max_t();

        double accel_maxvel_time = this.max_vel/this.max_accel;
        double halfway_t = max_t/2.0;
        double accel_int = 0.5 * this.max_accel * accel_maxvel_time * accel_maxvel_time;

        if (accel_int > halfway_t){
            accel_maxvel_time = Math.sqrt(2.0 * halfway_t / this.max_accel);
            accel_int = 0.5 * this.max_accel * accel_maxvel_time * accel_maxvel_time;
        }

        double max_velocity = this.max_accel * accel_maxvel_time;

        double constant_dist = max_t - 2.0 * accel_int;
        double constant_time = constant_dist/max_velocity;
        double decel_time = accel_maxvel_time + constant_time;

        if (realTime > accel_maxvel_time + constant_time + decel_time)
            return max_t;

        if (realTime < accel_maxvel_time)
            return 0.5 * this.max_accel * realTime * realTime;

        if (realTime < decel_time)
            return 0.5 * this.max_accel * accel_maxvel_time * accel_maxvel_time + max_velocity * (realTime - accel_maxvel_time);

        return 0.5 * this.max_accel * accel_maxvel_time * accel_maxvel_time + max_velocity * (realTime - accel_maxvel_time) - 0.5 * this.max_accel * Math.pow(realTime - accel_maxvel_time, 2);


    }

    public void tick(){
        if (this.currentSpline == null) return;

        double realTime = this.getTime();
        double t = this.trapezoidalMotionProfile(realTime);
        if (t > this.currentSpline.max_t()){
            this.currentSpline = null;
            return;
        }

        ManipulatorConfiguration config = this.currentSpline.inverseKinematics(t, this.reversed);
        this.controller.setConfiguration(config);
    }
}
