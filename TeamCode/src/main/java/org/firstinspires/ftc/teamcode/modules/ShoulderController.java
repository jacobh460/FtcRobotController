package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class ShoulderController extends ServoController{

    public static double kP = 0.25;
    public static double kI = 0;
    public static double kD = 0.0001;

    public static double kF = 0.12;

    public static double a = 0.8;

    private double lastError = 0.0;

    private double integral = 0.0;


    public ShoulderController(HardwareMap hardwareMap, String shoulderName, String inputName){
        super(hardwareMap, shoulderName, inputName, 101.563 + 15.4);
    }

    public ShoulderController(HardwareMap hardwareMap){
        this(hardwareMap, "shoulder", "shoulderIn");
    }


    private double lastTarget = 0.0;

    private double previousFilterEstimate = 0;
    private double PosPID(double target, double deltaTime){
        double angleRad = super.readAngleRAD();
        double error = target - angleRad;
        double errorChange = error-lastError;

        double currentFilterEstimate = (this.a * this.previousFilterEstimate) + (1-this.a) * errorChange;
        this.previousFilterEstimate = currentFilterEstimate;

        double derivative = currentFilterEstimate / deltaTime;

        this.lastError = error;

        integral += error * deltaTime;

        if (target != lastTarget) integral = 0.0;
        lastTarget = target;


        return -this.kP * error - derivative * this.kD - Math.cos(target) * this.kF - Math.min(Math.max(integral * this.kI, -0.25), 0.25);
    }

    public double doPID(double target, double deltaTime){
        double power = this.PosPID(target, deltaTime);
        this.output.setPower(power);
        return power;
    }
}
