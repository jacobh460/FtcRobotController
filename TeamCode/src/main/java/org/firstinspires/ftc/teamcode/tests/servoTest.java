package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class servoTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo servo = hardwareMap.get(Servo.class, "testServo");

        waitForStart();

        servo.setPosition(0.5);

        while (opModeIsActive()){
            sleep(5);
        }

        return;
    }
}
