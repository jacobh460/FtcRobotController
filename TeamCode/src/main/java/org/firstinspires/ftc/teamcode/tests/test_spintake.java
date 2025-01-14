package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(group="test")
public class test_spintake extends LinearOpMode {

    DcMotorEx motor;


    double interval = 50.0;//change in rpm when user presses a button

    final double resolution = 145.1;

    @Override
    public void runOpMode(){
        motor = hardwareMap.get(DcMotorEx.class, "spinTake");
        telemetry.setMsTransmissionInterval(50);

        double rpm = 100;

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocity(0);

        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        /*
        PIDFCoefficients coef = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        coef.i = 0.1;
        coef.p *= 3;
        coef.d *= 3;

        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, coef);*/
        waitForStart();

        boolean lu = false;
        boolean ll = false;

        while (opModeIsActive()){
            double velo = motor.getVelocity();
            telemetry.addData("Target Rpm", rpm);
            telemetry.addData("Real RPM", velo/resolution*60);
            telemetry.update();

            if (gamepad1.dpad_up && !lu) rpm += interval;
            if (gamepad1.dpad_down && !ll) rpm -= interval;

            lu = gamepad1.dpad_up;
            ll = gamepad1.dpad_down;

            double tick_speed = rpm / 60.0 * resolution;

            motor.setVelocity(gamepad1.right_bumper ? tick_speed : (gamepad1.left_bumper ? -tick_speed : 0));


            sleep(5);
        }
    }
}
