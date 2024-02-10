package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;



@TeleOp(group="test")
public class test_analog extends LinearOpMode {


    @Override
    public void runOpMode(){
        AnalogInput analogInput = this.hardwareMap.get(AnalogInput.class, "shoulderIn");
        CRServo servo = this.hardwareMap.get(CRServo.class, "shoulder");
        servo.setPower(0.0);

        waitForStart();

        double zero = 101.563 + 15.4;

        while (opModeIsActive()){
            telemetry.addData("Reading", analogInput.getVoltage()/3.3 * 360.0 - zero);
            telemetry.update();
        }
    }
}
