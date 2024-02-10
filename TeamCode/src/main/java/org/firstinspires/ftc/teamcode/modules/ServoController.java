package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ServoController {


    public AnalogInput input;
    public CRServo output;

    private double rom = 360.0;

    private double romRAD = 2 * Math.PI;

    private double zeroOffsetRAD;
    private double zeroOffsetDeg;
    public ServoController(HardwareMap hardwareMap, String servoName, String analogName, double zeroOffsetDeg){
        this.input = hardwareMap.get(AnalogInput.class, analogName);
        this.output = hardwareMap.get(CRServo.class, servoName);

        this.zeroOffsetDeg = zeroOffsetDeg;
        this.zeroOffsetRAD = Math.toRadians(this.zeroOffsetDeg);

        output.setPower(0.0);
    }

    public double readAngle(){
        return this.input.getVoltage() * this.rom / 3.3 - this.zeroOffsetDeg;
    }

    public double readAngleRAD(){
        return this.input.getVoltage() * this.romRAD / 3.3 - this.zeroOffsetRAD;
    }

}
