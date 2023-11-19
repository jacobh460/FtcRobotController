package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(group="test")
public class lift_test extends LinearOpMode {

    double maxSpeed = 400;
    int maxPos = 2100;

    @Override
    public void runOpMode(){
        DcMotorEx leftMotor = hardwareMap.get(DcMotorEx.class, "leftLift");
        DcMotorEx rightMotor = hardwareMap.get(DcMotorEx.class, "rightLift");

        leftMotor.setMotorEnable();
        rightMotor.setMotorEnable();


        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        int position = 0;


        double lastTime = getRuntime();
        while (opModeIsActive()){
            double curTime = getRuntime();
            double deltaTime = curTime - lastTime;
            lastTime = curTime;

            position += (int)(gamepad1.left_stick_y * deltaTime * maxSpeed);

            position = Math.min(Math.max(0, position), maxPos);


            leftMotor.setTargetPosition(position);
            rightMotor.setTargetPosition(position);
            leftMotor.setVelocity(maxSpeed);
            rightMotor.setVelocity(maxSpeed);

            leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            telemetry.addData("Y", gamepad1.left_stick_y);
            telemetry.addData("Target Position", position);
            telemetry.update();

            sleep(5);
        }



    }
}
