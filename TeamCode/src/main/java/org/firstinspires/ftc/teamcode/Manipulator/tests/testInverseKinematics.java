package org.firstinspires.ftc.teamcode.Manipulator.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Manipulator.InverseKinematics;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorConfiguration;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;
import org.firstinspires.ftc.teamcode.Manipulator.Vector2;


@TeleOp(group="test")
public class testInverseKinematics extends LinearOpMode {

    @Override
    public void runOpMode(){

        ManipulatorController controller = new ManipulatorController(this.hardwareMap);
        Vector2 target = new Vector2(9, 7);
        double speed = 10.0;//inches/second

        this.telemetry.setMsTransmissionInterval(20);
        waitForStart();

        double lastTime = getRuntime();
        while (opModeIsActive()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;

            target.x += gamepad1.left_stick_x * deltaTime * speed;
            target.y -= gamepad1.left_stick_y * deltaTime * speed;

            target.x += ((gamepad1.dpad_left ? -1.0 : 0) + (gamepad1.dpad_right ? 1.0 : 0)) * deltaTime * speed;
            target.y += ((gamepad1.dpad_up ? 1.0 : 0) + (gamepad1.dpad_down ? -1.0 : 0 )) * deltaTime * speed;

            ManipulatorConfiguration config = InverseKinematics.doInverseKinematics(target);
            if (config.isValid())
                controller.setConfiguration(config);


            telemetry.addData("Hz", 1/deltaTime);
            telemetry.addData("Config", config);
            telemetry.addData("Target", target);
            telemetry.update();
        }
    }
}
