package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;

@Autonomous(preselectTeleOp = "drivermode")
public class redRight extends blueBaseAuto {

    @Override
    public TrajectorySequence createTrajectory(){
        this.manipulator.hand.close();
        sleep(700);
        this.manipulator.floatingPickupPreset();


        this.drive.setPoseEstimate(new Pose2d(14.00, -61.75, Math.toRadians(-90.00)));
        TrajectorySequenceBuilder sequenceBuilder = drive.trajectorySequenceBuilder(new Pose2d(14.00, -61.75, Math.toRadians(-90.00)))
                .addDisplacementMarker(() -> {
                    this.manipulator.dropOffPreset();
                })
                .setReversed(true)
                .lineToConstantHeading(new Vector2d(36.00, -48.00))
                .lineToLinearHeading(new Pose2d(45.5, -36.00, Math.toRadians(-180.00)))
                .waitSeconds(1)
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    this.manipulator.hand.open();
                })
                .waitSeconds(0.1)
                .UNSTABLE_addTemporalMarkerOffset(0, ()-> {
                    this.manipulator.wrist.setPosition(this.manipulator.wrist.getPosition() + 0.05);
                })
                .waitSeconds(0.1)
                .lineToConstantHeading(new Vector2d(40.00, -36.00))
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    this.manipulator.travelingPreset();
                })
                .lineToLinearHeading(new Pose2d(40.00, -62.00, Math.toRadians(-270.00)))

                .lineToConstantHeading(new Vector2d(60.00, -62.00))
                .setReversed(false);





        return sequenceBuilder.build();
    }
}
