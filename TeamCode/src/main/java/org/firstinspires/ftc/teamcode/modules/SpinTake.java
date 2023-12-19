package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SpinTake {

    private DcMotorEx intake = null;

    private double resolution = 145.1;
    private double ticksPerRad = resolution/(Math.PI*2);
    public SpinTake(HardwareMap hardwareMap, String motorName){
        this.intake = hardwareMap.get(DcMotorEx.class, motorName);
        this.intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.intake.setPower(0);
        this.intake.setMotorDisable(); //so we can freely spin it by hand if we need during initialization
    }

    public SpinTake(HardwareMap hardwareMap){
        this(hardwareMap, "spinTake");
    }


    public void enable(){
        this.intake.setMotorEnable();
    }

    public void disable(){
        this.intake.setMotorDisable();
    }

    public boolean isEnabled(){
        return this.intake.isMotorEnabled();
    }

    public DcMotor.RunMode getMode(){
        return this.intake.getMode();
    }
    public void setVelocity(double rpm){
        double tickVelo = rpm/60.0 * resolution;
        this.intake.setVelocity(tickVelo);
    }

    public void runAtVelo(double rpm){
        this.setVelocity(rpm);
        this.intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public double getAngle(){
        return (this.intake.getCurrentPosition()/this.resolution * 360.0);
    }

    public void goToAngle(double angle, double velo){
        int ticks = (int)(angle/360.0 * this.resolution);
        this.intake.setTargetPosition(ticks);
        this.setVelocity(velo);
        this.intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

}
