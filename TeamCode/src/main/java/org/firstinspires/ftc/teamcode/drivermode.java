package org.firstinspires.ftc.teamcode;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.ActionScheduler;
import org.firstinspires.ftc.teamcode.modules.AirplaneHolder;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.SpinTake;

import java.util.List;

@TeleOp(group="competition")
public class drivermode extends LinearOpMode {


    Hand hand;

    SpinTake spinTake;

    SampleMecanumDrive drive;

    ManipulatorController manipulator;

    private final double handManualSpeed = 0.1;
    private final double wristManualSpeed = Math.toRadians(25.0);
    private final double shoulderManualSpeed = Math.toRadians(25.0);


    private final double spintakeIdleAngle = 100.0;

    private double spinTarget = 0;

    private boolean disableSpintake = false;

    static final double dpad_power = 0.35;


    private boolean hasLaunched = false;

    private ActionScheduler scheduler;

    private AirplaneHolder airplaneHolder;
    CRServo launcher;

    @Override
    public void runOpMode(){

        // Initialize different robot modules

        this.hand = new Hand(this.hardwareMap);
        this.spinTake = new SpinTake(this.hardwareMap);

        this.airplaneHolder = new AirplaneHolder(this.hardwareMap, "airplaneHolder");
        this.airplaneHolder.close();

        this.manipulator = new ManipulatorController(this.hardwareMap);

        this.scheduler = new ActionScheduler();


        this.launcher = hardwareMap.get(CRServo.class, "launcher");
        this.launcher.setPower(0);


        this.drive = new SampleMecanumDrive(hardwareMap, false);

        this.resetPose();


        //reset lift encoders and energize lift motors
        this.manipulator.lift.reset();
        this.manipulator.lift.enable();

        //start lift in traveling preset - auto leaves lift resting on crossbar
        this.manipulator.travelingPreset();
        //this.manipulator.setConfiguration(new ManipulatorConfiguration(Lift.minLength, Math.toRadians(20.0), Math.toRadians(0.0)));


        //this helps loop times a bit
        List<LynxModule> modules = this.hardwareMap.getAll(LynxModule.class);

        for (LynxModule module : modules){
            module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }



        telemetry.addLine("Ready to Start");
        telemetry.update();
        waitForStart();


        double lastTime = getRuntime();
        while (opModeIsActive()){

            //clear cached values
            for (LynxModule module : modules){
                module.clearBulkCache();
            }

            double curTime = getRuntime();
            double deltaTime = curTime - lastTime;
            lastTime = curTime;

            this.gamepad1Control(deltaTime);
            this.gamepad2Control(deltaTime);


            //check if any scheduled actions need to be run and run them
            scheduler.tick();

            telemetry.addData("Lift Length Target (inches)", this.manipulator.lift.getTargetLength());
            telemetry.addData("Wrist Servo", Math.toDegrees(this.manipulator.wrist.getTargetAngle()));
            telemetry.addData("Shoulder Servo", Math.toDegrees(this.manipulator.shoulder.getTargetAngle()));
            telemetry.addData("Hand Servo", this.hand.getPosition());
            telemetry.addData("Spintake Angle", this.spinTake.getAngle());
            telemetry.addData("Heading", Math.toDegrees(this.drive.getPoseEstimate().getHeading()));
            telemetry.addData("Stored Heading", Math.toDegrees(PoseStorage.savedPosition.getHeading()));
            telemetry.addData("Loop Rate", (1/deltaTime) + " Hz");
            telemetry.update();
        }
    }

    boolean wristDropping = false;

    void openHand(){
        if (this.wristDropping) return;
        this.wristDropping = true;
        this.hand.open();
        //we want to drop the hand to help the pixels fall out, then raise the hand again to return back to the previous position
        this.scheduler.addAction(() -> {
            double originalAngle = this.manipulator.wrist.getTargetAngle();
            this.manipulator.wrist.setAngle(originalAngle + Math.toRadians(25.0));
            this.scheduler.addAction(() -> {
                this.manipulator.wrist.setAngle(originalAngle);
                this.wristDropping = false;
            }, 300);
        }, 200);
    }

    //handle gamepad1 input
    private void gamepad1Control(double deltaTime){

        //press both triggers in to reset pose
        if (this.gamepad1.left_stick_button && this.gamepad1.right_stick_button) this.resetPose();

        {
            if (this.gamepad1.right_trigger > 0.01){
                this.disableSpintake = false;
                this.spinTake.enable();
                this.spinTake.runAtRPM(600.0);//run at this rpm

            }
            else if (this.gamepad1.left_trigger > 0.01){
                this.disableSpintake = false;
                this.spinTake.enable();
                this.spinTake.runAtRPM(-100.0 * this.gamepad1.left_trigger);//run at this rpm
            }
            else if (this.spinTake.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                double curAngle = this.spinTake.getAngle();
                this.spinTarget = curAngle + (360.0 - (curAngle % (360.0))) - 90.0;
                if (this.spinTarget < curAngle) this.spinTarget += 360.0;
                this.spinTake.goToAngle(this.spinTarget, 700.0);
            }
            telemetry.addData("Spintake Target Angle", this.spinTarget);
        }


        {
            Pose2d poseEstimate = this.drive.getPoseEstimate();

            if (gamepad1.dpad_up || gamepad1.dpad_down || gamepad1.dpad_right || gamepad1.dpad_left){
                Vector2d input = new Vector2d(
                        drivermode.dpad_power * (double)((gamepad1.dpad_up ? 1 : 0) + (gamepad1.dpad_down ? -1 : 0)),
                        drivermode.dpad_power * (double)((gamepad1.dpad_left ? 1 : 0) + (gamepad1.dpad_right ? -1 : 0))
                );
                this.drive.setWeightedDrivePower(new Pose2d(input.getX(), input.getY(), 0.0));
            }
            else{
                //cubic for better control maybe??
                Vector2d input = new Vector2d(
                        -Math.pow(gamepad1.left_stick_y, 3),
                        -Math.pow(gamepad1.left_stick_x, 3)
                ).rotated(-poseEstimate.getHeading());

                double yawPower = -gamepad1.right_stick_x * 0.75;

                this.drive.setWeightedDrivePower(
                        new Pose2d(
                                input.getX(),
                                input.getY(),
                                yawPower
                        )
                );
            }

            // Update everything. Odometry. Etc.
            this.drive.update();

            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
        }


        //async routine to launch airplane
        if (this.gamepad1.right_bumper && !this.hasLaunched){
            this.airplaneHolder.open();
            this.hasLaunched = true;
            this.scheduler.addAction(
                    () -> {
                        this.launcher.setPower(-.1);
                        this.scheduler.addAction(() -> {
                            this.launcher.setPower(0);
                            this.airplaneHolder.close();
                        }, 350);
                    },
                    333
            );
        }

        if (this.gamepad1.a)
            this.openHand();

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
            if (this.gamepad2.left_bumper) this.openHand();
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
