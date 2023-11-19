package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.modules.Hand;

@TeleOp(group="test")
public class handTest_V2 extends LinearOpMode {

    @Override
    public void runOpMode(){
        Hand hand = new Hand(hardwareMap, "testServo");

        waitForStart();


        while (opModeIsActive()){

            if (gamepad1.right_bumper) hand.close();
            else if (gamepad1.left_bumper) hand.open();

        }
    }
}
