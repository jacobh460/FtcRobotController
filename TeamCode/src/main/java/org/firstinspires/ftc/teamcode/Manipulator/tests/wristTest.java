package org.firstinspires.ftc.teamcode.Manipulator.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorConfiguration;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;
import org.firstinspires.ftc.teamcode.modules.Lift;


@TeleOp(group = "test")
public class wristTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        ManipulatorController controller = new ManipulatorController(this.hardwareMap);
        controller.setConfiguration(new ManipulatorConfiguration(Lift.minLength, Math.toRadians(45.0), Math.toRadians(0)));

        waitForStart();


        while (opModeIsActive()){
            if (gamepad1.a)
                controller.setConfiguration(new ManipulatorConfiguration(Lift.minLength, Math.toRadians(45.0), Math.toRadians(0)));
            else if (gamepad1.b)
                controller.setConfiguration(new ManipulatorConfiguration(Lift.minLength, Math.toRadians(45.0), Math.toRadians(90.0)));
        }
    }
}
