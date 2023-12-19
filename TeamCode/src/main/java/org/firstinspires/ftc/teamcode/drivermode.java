package org.firstinspires.ftc.teamcode;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.modules.SpinTake;
@TeleOp(group="competition")
public class drivermode extends LinearOpMode {

    Lift lift;

    Hand hand;

    Servo shoulder;
    Servo wrist;
    SpinTake spinTake;

    SampleMecanumDrive drive;

    private final double handManualSpeed = 0.35;
    private final double wristManualSpeed = 0.35;
    private final double shoulderManualSpeed = 0.35;


    private final double spintakeIdleAngle = 100.0;

    private double spinTarget = 0;

    private boolean disableSpintake = false;

    Servo launcher;

    @Override
    public void runOpMode(){
        this.lift = new Lift(this.hardwareMap);
        this.hand = new Hand(this.hardwareMap);
        this.spinTake = new SpinTake(this.hardwareMap);


        this.shoulder = hardwareMap.get(Servo.class, "shoulder");
        this.wrist = hardwareMap.get(Servo.class, "wrist");

        this.launcher = hardwareMap.get(Servo.class, "launcher");
        this.launcher.setPosition(0.5);


        this.drive = new SampleMecanumDrive(hardwareMap);

        this.resetPose();

        telemetry.setMsTransmissionInterval(50);



        waitForStart();
        this.travelingPreset();

        //reset lift encoders and energize lift motors
        this.lift.reset();
        this.lift.enable();


        double lastTime = getRuntime();
        while (opModeIsActive()){
            double curTime = getRuntime();
            double deltaTime = curTime - lastTime;
            lastTime = curTime;

            this.gamepad1Control(deltaTime);
            this.gamepad2Control(deltaTime);

            telemetry.addData("Lift Height Target (ticks)", this.lift.getTargetPosition());
            telemetry.addData("Wrist Servo", this.wrist.getPosition());
            telemetry.addData("Shoulder Servo", this.shoulder.getPosition());
            telemetry.addData("Hand Servo", this.hand.getPosition());
            telemetry.addData("Spintake Angle", this.spinTake.getAngle());
            telemetry.addData("Heading", Math.toDegrees(this.drive.getPoseEstimate().getHeading()));
            telemetry.addData("Stored Heading", Math.toDegrees(PoseStorage.savedPosition.getHeading()));
            telemetry.update();
        }


    }

    //handle gamepad1 input
    private void gamepad1Control(double deltaTime){
        /*
        {
            if (this.gamepad1.right_trigger > 0.02 || this.gamepad1.left_trigger > 0.02){
                if (!this.spinTake.isEnabled()) this.spinTake.enable();
                this.spinTake.runAtVelo(500.0 * (this.gamepad1.right_trigger - this.gamepad1.left_trigger));
            }
            else{
                this.spinTake.disable();
            }

        }*/

        //press both triggers in to reset pose
        if (this.gamepad1.left_stick_button && this.gamepad1.right_stick_button) this.resetPose();

        {
            if (this.gamepad1.right_trigger > 0.02){
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
            Pose2d poseEstimate = this.drive.getPoseEstimate();

            // Create a vector from the gamepad x/y inputs
            // Then, rotate that vector by the inverse of that heading
            Vector2d input = new Vector2d(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x
            ).rotated(-poseEstimate.getHeading());

            // Pass in the rotated input + right stick value for rotation
            // Rotation is not part of the rotated input thus must be passed in separately
            this.drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX(),
                            input.getY(),
                            -gamepad1.right_stick_x
                    )
            );

            // Update everything. Odometry. Etc.
            this.drive.update();

            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));


        }

        this.launcher.setPosition(gamepad1.right_bumper ? 0.45 : 0.5);

    }

    //handle gamepad2 input
    private void gamepad2Control(double deltaTime){

        //adjusting lift height at constant speed with dpad up/down
        {
            int targetPosition = this.lift.getTargetPosition();
            int deltaX = (int)(this.lift.speed * deltaTime);
            this.lift.setTargetPosition(targetPosition + (this.gamepad2.dpad_up ? deltaX : 0) - (this.gamepad2.dpad_down ? deltaX : 0));
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
            //position A (pick up pixel from passthrough)
            if (gamepad2.a){
                pickupPreset();
            }


            //position B (drop off pixel)
            else if (gamepad2.x) {
                dropOffPreset();
            }


            //position Y (above pick up zone)
            else if (gamepad2.b)
            {
                floatingPickupPreset();
            }


            //position X (traveling)
            else if (gamepad2.y) {
                travelingPreset();
            }
        }

        //manual shoulder/wrist control
        {
            double newWristPos = this.wrist.getPosition() - this.gamepad2.left_stick_y * this.wristManualSpeed * deltaTime;
            double newShoulderPos = this.shoulder.getPosition() - this.gamepad2.right_stick_y * this.shoulderManualSpeed * deltaTime;

            newWristPos = Math.min(Math.max(0, newWristPos), 1);
            newShoulderPos = Math.min(Math.max(0, newShoulderPos), 1);

            this.shoulder.setPosition(newShoulderPos);
            this.wrist.setPosition(newWristPos);
        }


    }
    private void travelingPreset(){
        lift.setTargetPosition(0);
        wrist.setPosition(0.4416666);
        shoulder.setPosition(0.1288888);
    }
    private void floatingPickupPreset(){
        lift.setTargetPosition(0);
        wrist.setPosition(0.16111111111);
        shoulder.setPosition(0.3083333333);
    }

    private void dropOffPreset(){
        lift.setTargetPosition(1150);
        wrist.setPosition(0.81944444);
        shoulder.setPosition(0.514444);
    }


    private void pickupPreset(){
        /*
        lift.setTargetPosition(0);
        wrist.setPosition(0.685);
        shoulder.setPosition(0.27111 + shoulderOffset);
        */
        hand.open();
        lift.setTargetPosition(0);
        wrist.setPosition(0.222777777);
        shoulder.setPosition(0.023888888);
    }

    private void resetPose(){
        this.drive.setPoseEstimate(
                new Pose2d(
                        0, 0, 0
                )
        );
    }
}
