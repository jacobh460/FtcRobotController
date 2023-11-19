package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


@TeleOp(group="test")
public class lift_motor_position extends LinearOpMode {

    @Override
    public void runOpMode(){
        DcMotorEx leftMotor = hardwareMap.get(DcMotorEx.class, "leftLift");
        DcMotorEx rightMotor = hardwareMap.get(DcMotorEx.class, "rightLift");

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        waitForStart();

        telemetry.setMsTransmissionInterval(40);

        while (opModeIsActive()){
            telemetry.addData("Left Motor Position", leftMotor.getCurrentPosition());
            telemetry.addData("Right Motor Position", rightMotor.getCurrentPosition());
            telemetry.update();


            sleep(40);
        }
    }
}
