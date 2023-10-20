package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SpinTake {

    private DcMotorEx intake = null;

    //assuming Yellow Jacket 5203-2402-0019 Planetary Gear Motor
    private double resolution = 537.7;
    private double ticksPerRad = resolution/(Math.PI*2);
    public SpinTake(HardwareMap hardwareMap, String motorName){
        intake = hardwareMap.get(DcMotorEx.class, motorName);
        intake.setPower(0);
        intake.setMotorDisable(); //so we can freely spin it by hand if we need during initialization
    }

    /**
     * energize motor - must run this after initialization
     */
    public void Enable(){
        intake.setMotorEnable();
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//reset current rotation to 0
    }

    /**
     * @param Velocity angular velocity in radians/s
     */
    public void SetAngularVelocity(double Velocity){
        this.intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.intake.setVelocity(Velocity * ticksPerRad);
    }

    /**
     * @param Velocity angular velocity in degrees/s
     */

    public void SetAngularVelocityDeg(double Velocity){
        this.SetAngularVelocity(
                Math.toRadians(Velocity)
        );
    }


    //todo: create a method which allows the spintake to point away from or "avoid" a specific rotation, which runs using RunMode.RUN_TO_POSITION (making room for the hand to pick stuff up)
}
