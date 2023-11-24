package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(group="test")
public class test_spintake_encoder extends LinearOpMode {

    private double resolution = 145.1;

    @Override
    public void runOpMode(){
        DcMotorEx spinTake = this.hardwareMap.get(DcMotorEx.class, "spinTake");
        spinTake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spinTake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        telemetry.setMsTransmissionInterval(50);

        while (opModeIsActive()){
            this.telemetry.addData("position", spinTake.getCurrentPosition()/this.resolution * 360.0);
            this.telemetry.update();
        }
    }
}
