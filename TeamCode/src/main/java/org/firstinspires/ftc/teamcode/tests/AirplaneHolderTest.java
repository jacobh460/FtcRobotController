package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(group="test")
public class AirplaneHolderTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo servo = hardwareMap.get(Servo.class, "airplaneHolder");

        waitForStart();

        double target = 0.45;


        double lastTime = getRuntime();
        while (opModeIsActive()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;

            target -= this.gamepad1.left_stick_y * deltaTime * 0.25;

            servo.setPosition(target);
            telemetry.addData("target", target);
            telemetry.update();
        }

    }
}
