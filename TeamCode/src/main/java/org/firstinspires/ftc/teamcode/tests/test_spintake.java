package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class test_spintake extends LinearOpMode {

    DcMotorEx motor;


    double interval = 10.0;

    double resolution = 145.1;

    @Override
    public void runOpMode(){
        motor = hardwareMap.get(DcMotorEx.class, "motor0");
        telemetry.setMsTransmissionInterval(50);

        double speed = 50.0;

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocity(0);

        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        PIDFCoefficients coef = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        coef.i = 0.1;
        coef.p *= 3;
        coef.d *= 3;

        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, coef);
        waitForStart();

        boolean lu = false;
        boolean ll = false;

        while (opModeIsActive()){
            double velo = motor.getVelocity(AngleUnit.DEGREES);
            telemetry.addData("Target Velocity (ticks/s)", speed);
            telemetry.addData("Target Velocity (deg/s)", speed/resolution);
            telemetry.addData("Real Velocity (deg/s)", velo);
            telemetry.addData("Real RPM", velo * 60);
            telemetry.update();

            if (gamepad1.dpad_up && !lu) speed += interval;
            if (gamepad1.dpad_down && !ll) speed -= interval;

            lu = gamepad1.dpad_up;
            ll = gamepad1.dpad_down;

            motor.setVelocity(gamepad1.right_bumper ? speed : (gamepad1.left_bumper ? -speed : 0));


            sleep(5);
        }
    }
}
