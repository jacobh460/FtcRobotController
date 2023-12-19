package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class launcherTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo launcher = hardwareMap.get(Servo.class, "launcher");

        launcher.setPosition(0.5);
        waitForStart();



        while (opModeIsActive()){
            launcher.setPosition(gamepad1.right_bumper ? 0.45 : 0.5);

            launcher.setPosition(launcher.getPosition());
            telemetry.addData("Launcher Position: ", launcher.getPosition());
            telemetry.update();
        }
    }
}
