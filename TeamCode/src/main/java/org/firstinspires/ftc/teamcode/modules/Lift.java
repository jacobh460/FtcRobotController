package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift{

    private DcMotorEx leftLift;
    private DcMotorEx rightLift;

    private double targetLength = 0; //this is in inches

    public final double speed = 12.0;//target speed inches/second

    public static final double maxLength = 23; //max length the lift can extend, inches

    public static final double minLength = (5.0 + 3.0/8.0)/Math.sin(Math.toRadians(122.3));
    private final double spoolDiameter = 1.40358268;//inches

    private final double spoolCircumference = this.spoolDiameter * Math.PI;

    private final double resolution = 384.5;


    public Lift(HardwareMap hardwareMap, String leftLiftName, String rightLiftName){
        this.leftLift = hardwareMap.get(DcMotorEx.class, leftLiftName);
        this.rightLift = hardwareMap.get(DcMotorEx.class, rightLiftName);
        this.leftLift.setDirection(DcMotorSimple.Direction.REVERSE);

        this.disable();//disable motors so they can be adjusted during init phase
    }

    public Lift(HardwareMap hardwareMap){
        this(hardwareMap, "leftLift", "rightLift");
    }

    public void disable(){
        this.leftLift.setMotorDisable();
        this.rightLift.setMotorDisable();
    }

    //speed is in inches per second
    private void setSpeed(double speed){
        double rawVelocity = speed / this.spoolCircumference * this.resolution;
        this.leftLift.setVelocity(rawVelocity);
        this.rightLift.setVelocity(rawVelocity);
    }

    public void enable(){
        this.leftLift.setMotorEnable();
        this.rightLift.setMotorEnable();
    }

    public void reset(){
        this.leftLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void runToPosition(){
        this.leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    //returns target length of lift in inches
    public double getTargetLength() { return this.targetLength; }


    private void updateTarget(double targetLength){
        this.targetLength = targetLength;
        int targetPosition = (int)((targetLength - Lift.minLength) / this.spoolCircumference * this.resolution);
        this.leftLift.setTargetPosition(targetPosition);
        this.rightLift.setTargetPosition(targetPosition);
    }

    public void setTargetLength(double targetLength){
        targetLength = Math.min(Math.max(Lift.minLength, targetLength), Lift.maxLength);
        this.updateTarget(targetLength);

        this.setSpeed(this.speed); //default speed maybe change later

        this.runToPosition();
    }
}
