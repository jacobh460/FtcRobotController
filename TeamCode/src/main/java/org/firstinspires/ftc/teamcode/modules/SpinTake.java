package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class SpinTake {

    private DcMotorEx intake = null;

    private double resolution = 145.1;
    private double ticksPerRad = resolution/(Math.PI*2);
    public SpinTake(HardwareMap hardwareMap, String motorName){
        this.intake = hardwareMap.get(DcMotorEx.class, motorName);
        this.intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.intake.setPower(0);
        this.intake.setMotorDisable(); //so we can freely spin it by hand if we need during initialization

        PIDFCoefficients coef1 = this.intake.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        coef1.i = 0.0;
        this.intake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, coef1);

        PIDFCoefficients coef2 = this.intake.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
        coef2.i = 0.0;
        this.intake.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, coef2);
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
    public void setRPM(double rpm){
        double tickVelo = rpm/60.0 * resolution;
        this.intake.setVelocity(tickVelo);
    }

    public void runAtRPM(double rpm){
        if (this.intake.getMode() != DcMotor.RunMode.RUN_USING_ENCODER) this.intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.setRPM(rpm);
    }

    public double getAngle(){
        return (this.intake.getCurrentPosition()/this.resolution * 360.0);
    }

    public void goToAngle(double angle, double velo){
        int ticks = (int)(angle/360.0 * this.resolution);
        this.intake.setTargetPosition(ticks);
        this.setRPM(velo);
        this.intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

}
