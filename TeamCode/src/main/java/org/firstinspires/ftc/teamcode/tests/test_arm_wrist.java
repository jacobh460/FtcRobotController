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


        shoulder.scaleRange(0, .2);
        wrist.scaleRange(0, 1.0);

        waitForStart();

        while (opModeIsActive()){

            double shoulderServoPos = Math.max(0, (gamepad1.left_stick_y+1)/2);
            double wristServoPos = Math.max(0, (gamepad1.right_stick_y+1)/2);

            shoulder.setPosition(shoulderServoPos);
            wrist.setPosition(wristServoPos);
        }

    }

}
