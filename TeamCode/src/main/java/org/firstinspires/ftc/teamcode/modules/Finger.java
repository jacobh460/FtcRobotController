package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Finger {

    public Servo servo;

    public Finger(HardwareMap hardwareMap, String servoName){
        this.servo = hardwareMap.get(Servo.class, servoName);
    }

    public Finger(HardwareMap hardwareMap){this(hardwareMap, "Finger");}


    public void up(){
        this.servo.setPosition(0.219201794);
    }

    public void down(){
        this.servo.setPosition(0.865186728);
    }
}
