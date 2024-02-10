package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class AirplaneHolder {

    Servo servo;
    public AirplaneHolder(HardwareMap hardwareMap, String servoName){
        this.servo = hardwareMap.get(Servo.class, servoName);
    }

    public void open(){
        this.servo.setPosition(0.530438);
    }

    public void close(){
        this.servo.setPosition(0.10711575);
    }
}
