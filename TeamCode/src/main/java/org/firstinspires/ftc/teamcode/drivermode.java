package org.firstinspires.ftc.teamcode;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.ManipulatorController;
import org.firstinspires.ftc.teamcode.modules.SpinTake;

@TeleOp(group="competition")
public class drivermode extends LinearOpMode {


    Hand hand;

    SpinTake spinTake;

    SampleMecanumDrive drive;

    ManipulatorController manipulator;

    private final double handManualSpeed = 0.35;
    private final double wristManualSpeed = 0.35;
    private final double shoulderManualSpeed = 0.35;

    private final double MAX_SLEW = 2.0;


    private final double spintakeIdleAngle = 100.0;

    private double spinTarget = 0;

    private boolean disableSpintake = false;

    Servo launcher;


    private Pose2d lastInputRaw = new Pose2d(0, 0, 0);

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

        this.manipulator.travelingPreset();


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

            telemetry.addData("Lift Height Target (ticks)", this.manipulator.lift.getTargetPosition());
            telemetry.addData("Wrist Servo", this.manipulator.wrist.getPosition());
            telemetry.addData("Shoulder Servo", this.manipulator.shoulder.getPosition());
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
            else if (this.spinTake.getMode() != DcMotor.RunMode.RUN_TO_POSITION && this.spinTake.isEnabled() && !this.disableSpintake){
                double curAngle = this.spinTake.getAngle();
                this.spinTarget = Math.floor(curAngle/120.0) * 120.0;
                this.spinTake.goToAngle(this.spinTarget, 500.0);
            }
            else{
                if (Math.abs(this.spinTake.getAngle()) - this.spinTarget  < 5.0){
                    this.disableSpintake = true;
                    this.spinTake.disable();
                }
                if (!this.disableSpintake)
                    this.spinTake.goToAngle(this.spinTarget, 500.0);
            }
            telemetry.addData("Spintake Target Angle", this.spinTarget);
        }


        {
            //slew rate dampening
            double maxChange = MAX_SLEW * deltaTime;

            Pose2d poseEstimate = this.drive.getPoseEstimate();

            Vector2d input = new Vector2d(
                    Math.min(Math.max(-gamepad1.left_stick_y, Math.max(lastInputRaw.getY() - maxChange, -1.0)), Math.min(lastInputRaw.getY() + maxChange, 1.0)),
                    Math.min(Math.max(-gamepad1.left_stick_x, Math.max(lastInputRaw.getX() - maxChange, -1.0)), Math.min(lastInputRaw.getX() + maxChange, 1.0))
            ).rotated(-poseEstimate.getHeading());

            double yawPower = Math.min(Math.max(-gamepad1.right_stick_x, lastInputRaw.getHeading() - maxChange), lastInputRaw.getHeading() + maxChange);

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

            lastInputRaw = new Pose2d(input, yawPower);
        }

        this.launcher.setPosition(gamepad1.right_bumper ? 0.45 : 0.5);

    }

    //handle gamepad2 input
    private void gamepad2Control(double deltaTime){

        //adjusting lift height at constant speed with dpad up/down
        {
            double targetLength = this.manipulator.lift.getTargetLength();
            int deltaX = (int)(this.manipulator.lift.speed * deltaTime);
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
            Boolean[] buttons = {this.gamepad2.a, this.gamepad2.x, this.gamepad2.b, this.gamepad2.y};
            Runnable[] presets = {manipulator::pickupPreset, manipulator::dropOffPreset, manipulator::floatingPickupPreset, manipulator::travelingPreset};

            for (int i = 0; i < buttons.length; ++i){
                if (!buttons[i]) continue;
                presets[i].run();
                break;
            }
        }

        //manual shoulder/wrist control
        {
            double newWristPos = this.manipulator.wrist.getPosition() - this.gamepad2.left_stick_y * this.wristManualSpeed * deltaTime;
            double newShoulderPos = this.manipulator.shoulder.getPosition() - this.gamepad2.right_stick_y * this.shoulderManualSpeed * deltaTime;

            newWristPos = Math.min(Math.max(0, newWristPos), 1);
            newShoulderPos = Math.min(Math.max(0, newShoulderPos), 1);

            this.manipulator.shoulder.setPosition(newShoulderPos);
            this.manipulator.wrist.setPosition(newWristPos);
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
