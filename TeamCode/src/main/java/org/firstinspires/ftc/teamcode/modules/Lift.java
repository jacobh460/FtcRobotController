package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Lift{

    private DcMotorEx leftLift;
    private DcMotorEx rightLift;

    private int targetPosition = 0; //this is in radians

    public final double speed = 12.0;//target speed inches/second

    private final double maxLength = 30.0; //max length the lift can extend, inches

    private final double spoolDiameter = 1.40358268;//inches

    private final double spoolCircumference = this.spoolDiameter * Math.PI;

    private final double resolution = 384.5; //TODO: MAKE SURE THIS VALUE IS CORRECT


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

    //returns target position in encoder ticks
    public int getTargetPosition(){
        return this.targetPosition;
    }

    //returns target length of lift in inches
    public double getTargetLength() { return this.targetPosition / this.resolution * this.spoolCircumference; }


    private void updateTarget(double targetLength){
        this.targetPosition = (int)(targetLength / this.spoolCircumference * this.resolution);
        this.leftLift.setTargetPosition(this.targetPosition);
        this.rightLift.setTargetPosition(this.targetPosition);
    }

    public void setTargetLength(double targetLength){
        targetLength = Math.min(Math.max(0, targetLength), this.maxLength);
        this.updateTarget(targetLength);

        this.setSpeed(this.speed); //default speed maybe change later

        this.runToPosition();
    }
}
