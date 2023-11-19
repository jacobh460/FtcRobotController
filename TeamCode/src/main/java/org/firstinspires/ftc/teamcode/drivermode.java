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
        this.shoulder.setPosition(0.36);
        this.wrist.setPosition(0.67);

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
            telemetry.update();
        }


    }

    //handle gamepad1 input
    private void gamepad1Control(double deltaTime){

        {
            if (this.gamepad1.right_trigger > 0.02 || this.gamepad1.left_trigger > 0.02){
                this.spinTake.runAtVelo(500.0 /*speed*/ * (this.gamepad1.right_trigger - this.gamepad1.left_trigger));
            }
            else if (this.spinTake.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                double currentPosition = this.spinTake.getAngle();
                this.spinTarget = (double)(((int)currentPosition/(int)360) + 1) * 360.0 + spintakeIdleAngle;
                this.spinTake.goToAngle(spinTarget, 100.0);
            }
            else{
                this.spinTake.goToAngle(spinTarget, 100.0);
            }
            telemetry.addData("spintake target", spinTarget);
            telemetry.addData("Run To Position", this.spinTake.getMode() == DcMotor.RunMode.RUN_TO_POSITION);
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
                lift.setTargetPosition(0);
                wrist.setPosition(0.64944444444444);
                shoulder.setPosition(0.285);
            }


            //position B (drop off pixel)
            else if (gamepad2.b) {
                lift.setTargetPosition(655);
                wrist.setPosition(0.1427777777777);
                shoulder.setPosition(0.719444444444);
            }


            //position Y (pick up pixel in back)


            //position X (traveling)
            else if (gamepad2.x){
                lift.setTargetPosition(0);
                wrist.setPosition(0.4177777777);
                shoulder.setPosition(0.3988888888);
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
}
