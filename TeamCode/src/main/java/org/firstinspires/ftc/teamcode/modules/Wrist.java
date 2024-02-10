package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Wrist {

    private Servo wristServo = null;
    //for pinching


    private double wristServoRange = 300.0;

    public Wrist(HardwareMap hardwareMap, String wristServoName){
        this.wristServo = hardwareMap.get(Servo.class, wristServoName);

    }

    private void SetWristRotation(double degrees){
        this.wristServo.setPosition(Math.min(Math.max(degrees/wristServoRange, 0), 1));
    }


}
