package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hand {

    private Servo handServo;
    public Hand(HardwareMap hardwareMap, String servoName){
        this.handServo = hardwareMap.get(Servo.class, servoName);
        this.handServo.scaleRange(0.5, 0.75);
        this.open();
    }

    public Hand(HardwareMap hardwareMap){
        this(hardwareMap, "gripper");
    }

    //opens hand
    public void open(){
        this.handServo.setPosition(0.6);
    }

    //closes hand
    public void close(){
        this.handServo.setPosition(0.95);
    }


    //these are for manual control
    public double getPosition(){
        return this.handServo.getPosition();
    }

    public void setPosition(double position){
        this.handServo.setPosition(position);
    }
}
