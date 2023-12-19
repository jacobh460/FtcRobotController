package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;

@Autonomous
public class blueRightParkMid extends blueBaseAuto {

    @Override
    public TrajectorySequence createTrajectory(){
        this.hand.open();
        this.travelingPreset();

        this.drive.setPoseEstimate(new Pose2d(-38.00, 61.75, Math.toRadians(-90.00)));

        TrajectorySequenceBuilder sequenceBuilder = drive.trajectorySequenceBuilder(new Pose2d(-38.00, 61.75, Math.toRadians(-90.00)))
                .splineToLinearHeading(new Pose2d(-38.00, 59.00, Math.toRadians(-90.00)), Math.toRadians(-90.00))
                .waitSeconds(10)
                .lineTo(new Vector2d(36.00, 59.00))
                .lineTo(new Vector2d(36.00, 14.00))
                .lineTo(new Vector2d(43.00, 14.00));

        return sequenceBuilder.build();
    }
}