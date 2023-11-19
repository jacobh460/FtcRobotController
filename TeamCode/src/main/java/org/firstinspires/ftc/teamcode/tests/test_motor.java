package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="test_drive", group="test")

public class test_motor extends LinearOpMode {

    DcMotorEx motor0 = null;

    public void runOpMode(){
        motor0 = this.hardwareMap.get(DcMotorEx.class, "motor0");

        waitForStart();

        motor0.setPower(0);

        while (this.opModeIsActive()) {
            motor0.setPower(gamepad1.left_stick_y);
        }

        motor0.setPower(0);

    }


}
