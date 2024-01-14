package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(group="test")
public class FingerTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo servo = this.hardwareMap.get(Servo.class, "Finger");

        double position = 0.25;
        waitForStart();
        telemetry.setMsTransmissionInterval(40);

        double lastTime = getRuntime();
        while (opModeIsActive()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;


            position -= gamepad1.left_stick_y * deltaTime;
            position = Math.min(Math.max(position, 0.0), 1.0);

            servo.setPosition(position);

            telemetry.addData("Position", position);
            telemetry.update();
        }
    }
}
