package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class launcherTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        CRServo launcher = hardwareMap.get(CRServo.class, "launcher");

        launcher.setPower(0);
        waitForStart();



        while (opModeIsActive()){
            launcher.setPower(gamepad1.right_bumper ? -0.1 : 0.0);

            telemetry.addData("Launcher Position: ", launcher.getPower());
            telemetry.update();
        }
    }
}
