package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorConfiguration;
import org.firstinspires.ftc.teamcode.Manipulator.ManipulatorController;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Finger;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.modules.SpinTake;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.Hashtable;
import java.util.List;

public abstract class baseAuto extends LinearOpMode {

    public interface step{
        public TrajectorySequence getNext();
    }

    int markerPosition = -1;
    SampleMecanumDrive drive;
    SpinTake spinTake;
    ManipulatorController manipulator;

    TelemetryMenu menu;
    Hashtable<String, String> settings;

    ManipulatorConfiguration dropoffPreset = new ManipulatorConfiguration(Lift.minLength+1.0, Math.toRadians(120.0), Math.toRadians(100.0));

    Finger finger;

    public step[] flow;

    @Override
    public void runOpMode(){
        this.drive = new SampleMecanumDrive(this.hardwareMap, false);

        List<LynxModule> allHubs = this.hardwareMap.getAll(LynxModule.class);

        for (LynxModule module : allHubs){
            module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        this.spinTake = new SpinTake(this.hardwareMap);
        this.finger = new Finger(this.hardwareMap);
        this.finger.down();
        this.menu = new TelemetryMenu();


        this.manipulator = new ManipulatorController(this.hardwareMap);

        this.manipulator.hand.close();
        sleep(250);

        this.initialize();

        telemetry.addLine("Ready to Start");
        telemetry.update();

        while (!isStarted() && !isStopRequested()){
            this.menu.update(this.gamepad1, this.telemetry);
            sleep(75);
        }
        this.settings = this.menu.save();

        if (isStopRequested()) return;
        double lastTime = getRuntime();

        for (int i = 0; i < this.flow.length && opModeIsActive(); ++i) {
            TrajectorySequence next = this.flow[i].getNext();
            if (next == null) break;

            this.drive.followTrajectorySequenceAsync(next);

            while(opModeIsActive() && this.drive.isBusy()){
                double newTime = getRuntime();
                double deltaTime = newTime - lastTime;
                lastTime = newTime;

                for (LynxModule module : allHubs){
                    module.clearBulkCache();
                }

                this.tick(deltaTime);
                this.drive.update();

                telemetry.addData("Loop Rate", (1/deltaTime) + "Hz");
                telemetry.update();
            }
        }

        while (!isStopRequested() && opModeIsActive()) sleep(200);

        this.end();
    }


    //called after the trajectory has ended or autonomous has been stopped
    abstract public void end();

    abstract public void initialize();

    abstract public void tick(double deltaTime);

}
