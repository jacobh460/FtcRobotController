package org.firstinspires.ftc.teamcode;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorConfiguration;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.modules.SpinTake;

@TeleOp(group="competition")
public class drivermode extends LinearOpMode {


    Hand hand;

    SpinTake spinTake;

    SampleMecanumDrive drive;

    ManipulatorController manipulator;

    private final double handManualSpeed = 0.05;
    private final double wristManualSpeed = Math.toRadians(25.0);
    private final double shoulderManualSpeed = Math.toRadians(25.0);


    private final double spintakeIdleAngle = 100.0;

    private double spinTarget = 0;

    private boolean disableSpintake = false;

    Servo launcher;

    @Override
    public void runOpMode(){

        this.hand = new Hand(this.hardwareMap);
        this.spinTake = new SpinTake(this.hardwareMap);

        this.manipulator = new ManipulatorController(this.hardwareMap);


        this.launcher = hardwareMap.get(Servo.class, "launcher");
        this.launcher.setPosition(0.5);


        this.drive = new SampleMecanumDrive(hardwareMap);

        this.resetPose();

        telemetry.setMsTransmissionInterval(50);



        //reset lift encoders and energize lift motors
        this.manipulator.lift.reset();
        this.manipulator.lift.enable();

        //this.manipulator.travelingPreset();
        this.manipulator.setConfiguration(new ManipulatorConfiguration(Lift.minLength, Math.toRadians(20.0), Math.toRadians(0.0)));

        telemetry.addLine("Ready to Start");
        telemetry.update();
        waitForStart();

        double lastTime = getRuntime();
        while (opModeIsActive()){
            double curTime = getRuntime();
            double deltaTime = curTime - lastTime;
            lastTime = curTime;

            this.gamepad1Control(deltaTime);
            this.gamepad2Control(deltaTime);

            telemetry.addData("Lift Length Target (inches)", this.manipulator.lift.getTargetLength());
            telemetry.addData("Wrist Servo", Math.toDegrees(this.manipulator.wrist.getTargetAngle()));
            telemetry.addData("Shoulder Servo", Math.toDegrees(this.manipulator.shoulder.getTargetAngle()));
            telemetry.addData("Hand Servo", this.hand.getPosition());
            telemetry.addData("Spintake Angle", this.spinTake.getAngle());
            telemetry.addData("Heading", Math.toDegrees(this.drive.getPoseEstimate().getHeading()));
            telemetry.addData("Stored Heading", Math.toDegrees(PoseStorage.savedPosition.getHeading()));
            telemetry.update();
        }
    }

    //handle gamepad1 input
    private void gamepad1Control(double deltaTime){

        //press both triggers in to reset pose
        if (this.gamepad1.left_stick_button && this.gamepad1.right_stick_button) this.resetPose();

        {
            if (this.gamepad1.right_trigger > 0.01){
                this.disableSpintake = false;
                this.spinTake.enable();
                this.spinTake.runAtVelo(700.0);//run at this rpm

            }
            else if (this.gamepad1.left_trigger > 0.01){
                this.disableSpintake = false;
                this.spinTake.enable();
                this.spinTake.runAtVelo(300.0 * this.gamepad1.left_trigger);//run at this rpm
            }
            else if (this.spinTake.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                double curAngle = this.spinTake.getAngle();
                this.spinTarget = curAngle + (360.0 - (curAngle % (360.0))) - 90.0;
                if (this.spinTarget < curAngle) this.spinTarget += 360.0;
                this.spinTake.goToAngle(this.spinTarget, 700.0);
            }
            else{
                this.spinTake.goToAngle(this.spinTarget, 700.0);
            }
            telemetry.addData("Spintake Target Angle", this.spinTarget);
        }


        {
            Pose2d poseEstimate = this.drive.getPoseEstimate();



            Vector2d input = new Vector2d(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x
            ).rotated(-poseEstimate.getHeading());

            double yawPower = -gamepad1.right_stick_x;

            this.drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX(),
                            input.getY(),
                            yawPower
                    )
            );

            // Update everything. Odometry. Etc.
            this.drive.update();

            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
        }

        this.launcher.setPosition(gamepad1.right_bumper ? 0.45 : 0.5);

        if (this.gamepad1.a)
            this.manipulator.hand.open();

    }

    //handle gamepad2 input
    private void gamepad2Control(double deltaTime){

        //adjusting lift height at constant speed with dpad up/down
        {
            double targetLength = this.manipulator.lift.getTargetLength();
            double deltaX = this.manipulator.lift.speed * deltaTime;
            this.manipulator.lift.setTargetLength(targetLength + (this.gamepad2.dpad_up ? deltaX : 0) - (this.gamepad2.dpad_down ? deltaX : 0));
        }

        //hand control w/bumpers
        {
            if (this.gamepad2.right_bumper) this.hand.close();
            if (this.gamepad2.left_bumper) this.hand.open();
        }

        //manual hand control with triggers
        {
            double newPos = this.hand.getPosition() + (gamepad2.right_trigger * deltaTime * this.handManualSpeed) - (gamepad2.left_trigger * deltaTime * this.handManualSpeed);
            newPos = Math.min(Math.max(newPos, 0), 1);
            this.hand.setPosition(newPos);
        }

        //preset positions
        {
            Boolean[] buttons = {this.gamepad2.a, this.gamepad2.b, this.gamepad2.y, this.gamepad2.x};

            for (int i = 0; i < buttons.length; ++i){
                if (!buttons[i]) continue;
                this.manipulator.setConfiguration(ManipulatorController.presets[i]);
                break;
            }
        }

        //manual shoulder/wrist control
        {
            double newWristPos = this.manipulator.wrist.getTargetAngle() - this.gamepad2.left_stick_y * this.wristManualSpeed * deltaTime;
            double newShoulderPos = this.manipulator.shoulder.getTargetAngle() - this.gamepad2.right_stick_y * this.shoulderManualSpeed * deltaTime;

            this.manipulator.shoulder.setAngle(newShoulderPos);
            this.manipulator.wrist.setAngle(newWristPos);
        }


    }

    private void resetPose(){
        this.drive.setPoseEstimate(
                new Pose2d(
                        0, 0, 0
                )
        );
    }
}
