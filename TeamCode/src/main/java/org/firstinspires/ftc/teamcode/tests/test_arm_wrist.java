package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(group="test")
public class test_arm_wrist extends LinearOpMode {

    Servo shoulder;
    Servo wrist;

    @Override
    public void runOpMode(){

        shoulder = hardwareMap.get(Servo.class, "shoulder");
        wrist = hardwareMap.get(Servo.class, "wrist");


        shoulder.setPosition(0.5);
        wrist.setPosition(0.5);

        waitForStart();

        double lastTime = getRuntime();
        while (opModeIsActive()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;


            shoulder.setPosition(shoulder.getPosition() - gamepad1.left_stick_y * deltaTime * 0.2);
            wrist.setPosition(wrist.getPosition() - gamepad1.right_stick_y * deltaTime * 0.2);

            telemetry.addData("Shoulder Position", shoulder.getPosition());
            telemetry.addData("Wrist Position", wrist.getPosition());
            telemetry.update();
        }

    }

}
