package org.firstinspires.ftc.teamcode.modules;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoWrapper {

    private Servo servo;
    private double ROM;

    private double zeroOffset;

    private double targetAngle = 0.0;

    /**
     *
     * @param hardwareMap
     * @param ServoName
     * @param ROM radians, full servo range of motion
     * @param zeroOffset radians, offset from servo 0 to be treated as real 0
     */
    public ServoWrapper(HardwareMap hardwareMap, String ServoName, double ROM, double zeroOffset){
        this.servo = hardwareMap.get(Servo.class, ServoName);
        this.ROM = ROM;
        this.zeroOffset = zeroOffset/ROM;
    }

    public void setAngle(double angle){
        double rawPosition = Math.min(Math.max(
                angle/this.ROM + this.zeroOffset
                , 0.0), 1.0);
        this.targetAngle = angle;
        this.servo.setPosition(rawPosition);
    }

    /**
     *
     * @return target angle in radians
     */
    public double getTargetAngle(){
        return this.targetAngle;
    }


}
