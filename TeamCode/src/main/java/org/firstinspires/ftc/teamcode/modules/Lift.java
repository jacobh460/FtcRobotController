package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift{

    private DcMotorEx leftLift;
    private DcMotorEx rightLift;

    private int targetPosition = 0;

    public final double speed = 800.0;//target speed ticks/s

    private int maxPosition = 2000; //max motor ticks

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

    private void setSpeed(double speed){
        this.leftLift.setVelocity(speed);
        this.rightLift.setVelocity(speed);
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

    public int getTargetPosition(){
        return this.targetPosition;
    }

    private void updateTarget(){
        this.leftLift.setTargetPosition(this.targetPosition);
        this.rightLift.setTargetPosition(this.targetPosition);
    }

    public void setTargetPosition(int target){
        this.targetPosition = Math.min(Math.max(0, target), maxPosition);
        this.updateTarget();


        this.setSpeed(this.speed); //default speed maybe change later

        this.runToPosition();
    }
}
