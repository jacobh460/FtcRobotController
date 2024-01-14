package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoWrapper {

    public Servo servo;
    private double ROM;

    private double zeroOffset;

    private double targetAngle = 0.0;

    private double multiplier;

    /**
     *
     * @param hardwareMap
     * @param ServoName
     * @param ROM radians, full servo range of motion
     * @param zeroOffset radians, offset from servo 0 to be treated as real 0
     */
    public ServoWrapper(HardwareMap hardwareMap, String ServoName, double ROM, double zeroOffset, double multiplier){
        this.servo = hardwareMap.get(Servo.class, ServoName);
        this.ROM = ROM;
        this.zeroOffset = zeroOffset;
        //this multiplier exists because I noticed 5-10 degrees of error on some of the servos and this should help account for that
        //to determine what value to set this to, set it to 1 initially then run and make the servo go 90 degrees (measure to make sure it is exact), then do (angle the servo thinks it is at) / (actual angle), after changing this you will need to recalculate zero offset
        this.multiplier = multiplier;
    }

    public void setAngle(double angle){
        double convertedAngle = (angle + this.zeroOffset) % (Math.PI * 2);
        if (convertedAngle < 0.0) convertedAngle += Math.PI * 2;

        double rawPosition = Math.min(Math.max(
                convertedAngle/this.ROM * this.multiplier
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
