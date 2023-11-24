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

    double shoulderOffset = 0.086666666666 - 0.275555555;

    private final double spintakeIdleAngle = 100.0;

    private double spinTarget = 0;

    private boolean disableSpintake = false;

    @Override
    public void runOpMode(){
        this.lift = new Lift(this.hardwareMap);
        this.hand = new Hand(this.hardwareMap);
        this.spinTake = new SpinTake(this.hardwareMap);


        this.shoulder = hardwareMap.get(Servo.class, "shoulder");
        this.wrist = hardwareMap.get(Servo.class, "wrist");



        this.drive = new SampleMecanumDrive(hardwareMap);

        this.drive.setPoseEstimate(
                new Pose2d(0, 0, Math.toRadians(0.0))
        );

        telemetry.setMsTransmissionInterval(50);



        waitForStart();
        passthroughPreset();

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
            telemetry.addData("Shoulder Servo", this.shoulder.getPosition() - this.shoulderOffset);
            telemetry.addData("Hand Servo", this.hand.getPosition());
            telemetry.addData("Spintake Angle", this.spinTake.getAngle());
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

        {
            if (this.gamepad1.right_trigger > 0.02 || this.gamepad1.left_trigger > 0.02){
                this.disableSpintake = false;
                this.spinTake.enable();
                this.spinTake.runAtVelo(1000.0 * (this.gamepad1.right_trigger - this.gamepad1.left_trigger));

            }
            else if (this.spinTake.getMode() != DcMotor.RunMode.RUN_TO_POSITION && this.spinTake.isEnabled() && !this.disableSpintake){
                double curAngle = this.spinTake.getAngle();
                this.spinTarget = Math.floor(curAngle/120.0) * 120.0;
                this.spinTake.goToAngle(this.spinTarget, 800.0);
            }
            else{
                if (Math.abs(this.spinTake.getAngle()) - this.spinTarget  < 5.0){
                    this.disableSpintake = true;
                    this.spinTake.disable();
                }
                if (!this.disableSpintake)
                    this.spinTake.goToAngle(this.spinTarget, 800.0);
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
                passthroughPreset();
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
        wrist.setPosition(0.356);
        shoulder.setPosition(0.3649999 + shoulderOffset);
    }
    private void floatingPickupPreset(){
        lift.setTargetPosition(0);
        wrist.setPosition(0.66833333);
        shoulder.setPosition(0.3988888888 + shoulderOffset);
    }

    private void dropOffPreset(){
        lift.setTargetPosition(655);
        wrist.setPosition(0);
        shoulder.setPosition(0.719444444444 + shoulderOffset);
    }


    private void passthroughPreset(){
        /*
        lift.setTargetPosition(0);
        wrist.setPosition(0.685);
        shoulder.setPosition(0.27111 + shoulderOffset);
        */
        hand.open();
        lift.setTargetPosition(0);
        wrist.setPosition(0.64944444444444);
        shoulder.setPosition(0.2588888 + shoulderOffset);
    }
}
