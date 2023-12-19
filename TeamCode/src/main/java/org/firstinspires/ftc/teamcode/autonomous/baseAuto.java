package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.modules.SpinTake;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

public abstract class baseAuto extends LinearOpMode {

    int markerPosition = -1;
    SampleMecanumDrive drive;
    SpinTake spinTake;

    Servo wrist;
    Servo shoulder;

    Lift lift;

    Hand hand;

    @Override
    public void runOpMode(){
        this.drive = new SampleMecanumDrive(this.hardwareMap);
        this.spinTake = new SpinTake(this.hardwareMap);
        this.lift = new Lift(this.hardwareMap);
        this.hand = new Hand(this.hardwareMap);

        this.shoulder = hardwareMap.get(Servo.class, "shoulder");
        this.wrist = hardwareMap.get(Servo.class, "wrist");



        TrajectorySequence sequence = this.createTrajectory();
        this.drive.followTrajectorySequenceAsync(sequence);


        waitForStart();
        if (isStopRequested()) return;
        double lastTime = getRuntime();
        while(opModeIsActive()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;

            this.drive.update();

        }

        this.end();
    }

    abstract public TrajectorySequence createTrajectory();
    abstract public void end();


    void travelingPreset(){
        lift.setTargetPosition(0);
        wrist.setPosition(0.4416666);
        shoulder.setPosition(0.1288888);
    }
    void floatingPickupPreset(){
        lift.setTargetPosition(0);
        wrist.setPosition(0.16111111111);
        shoulder.setPosition(0.3083333333);
    }

    void dropOffPreset(){
        lift.setTargetPosition(700);
        wrist.setPosition(0.81944444);
        shoulder.setPosition(0.514444);
    }


    void pickupPreset(){
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

}
