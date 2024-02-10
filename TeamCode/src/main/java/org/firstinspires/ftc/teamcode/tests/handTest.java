package org.firstinspires.ftc.teamcode.tests;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(group="test")
public class handTest extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo handServo = hardwareMap.get(Servo.class, "gripper");

        //handServo.scaleRange(0.5, 0.75);
        telemetry.setMsTransmissionInterval(20);

        waitForStart();

        double pos = 0.5;
        final double speed = 0.2;

        double lastTime = getRuntime();
        while (opModeIsActive()){
            double curTime = getRuntime();
            double dTime = curTime - lastTime;
            lastTime = curTime;

            pos += speed * gamepad1.left_stick_y * dTime;
            pos = Math.max(Math.min(pos, 1), 0);
            handServo.setPosition(pos);

            telemetry.addData("Position", pos);
            telemetry.update();
        }
    }

}
