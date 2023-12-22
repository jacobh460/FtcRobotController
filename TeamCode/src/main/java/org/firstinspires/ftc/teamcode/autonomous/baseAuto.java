package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.modules.ManipulatorController;
import org.firstinspires.ftc.teamcode.modules.SpinTake;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

public abstract class baseAuto extends LinearOpMode {

    int markerPosition = -1;
    SampleMecanumDrive drive;
    SpinTake spinTake;
    ManipulatorController manipulator;

    @Override
    public void runOpMode(){
        this.drive = new SampleMecanumDrive(this.hardwareMap);
        this.spinTake = new SpinTake(this.hardwareMap);


        this.manipulator = new ManipulatorController(this.hardwareMap);



        TrajectorySequence sequence = this.createTrajectory();
        this.drive.followTrajectorySequenceAsync(sequence);


        telemetry.addLine("Ready to Start");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;
        double lastTime = getRuntime();
        while(opModeIsActive() && this.drive.isBusy()){
            double newTime = getRuntime();
            double deltaTime = newTime - lastTime;
            lastTime = newTime;

            this.drive.update();
        }

        this.end();
    }

    //called after init, must return a TrajectorySequence
    abstract public TrajectorySequence createTrajectory();


    //called after the trajectory has ended or autonomous has been stopped
    abstract public void end();

}
