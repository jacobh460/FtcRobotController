package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(group="test")
public class test_shoulder extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo shoulder = hardwareMap.get(Servo.class, "shoulder");

        waitForStart();

        while (opModeIsActive()){
            shoulder.setPosition(Math.max(gamepad1.left_stick_y, 0));
        }
    }
}
