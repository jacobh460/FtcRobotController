package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Arm {
    private Wrist wrist = null;

    //when viewing from behind the arm
    private Servo leftServo = null;
    private Servo rightServo = null;

    private double maxAngle = 300.0;

    /**
     * @param degrees angle to rotate arm to in degrees
     */
    public void SetRotation(double degrees){
        //lock between 0 and 1
        double rot = Math.max(Math.min(degrees/maxAngle, 1.0), 0.0);
        leftServo.setPosition(rot);
        rightServo.setPosition(1.0-rot);
    }
    public Arm(HardwareMap hardwareMap, String leftServoName, String rightServoName, String wristServoName, String handServoName){
        this.leftServo = hardwareMap.get(Servo.class, leftServoName);
        this.rightServo = hardwareMap.get(Servo.class, rightServoName);
        this.wrist = new Wrist(hardwareMap, wristServoName, handServoName);
    }
}
