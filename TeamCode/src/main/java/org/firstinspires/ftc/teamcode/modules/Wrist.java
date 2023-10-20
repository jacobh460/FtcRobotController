package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Wrist {

    private Servo wristServo = null;
    //for pinching
    private Servo handServo = null;

    private double wristServoRange = 300.0;
    private double handServoRange = 300.0;
    public Wrist(HardwareMap hardwareMap, String wristServoName, String handServoName){
        this.wristServo = hardwareMap.get(Servo.class, wristServoName);
        this.handServo = hardwareMap.get(Servo.class, handServoName);
    }

    private void SetWristRotation(double degrees){
        this.wristServo.setPosition(Math.min(Math.max(degrees/wristServoRange, 0), 1));
    }

    public void Pinch(){
        //todo once we can test
    }

    public void Open(){
        //todo once we can test
    }

    public void SetHandRotation(double degrees){
        this.handServo.setPosition(Math.min(Math.max(degrees/handServoRange, 0), 1));
    }
}
