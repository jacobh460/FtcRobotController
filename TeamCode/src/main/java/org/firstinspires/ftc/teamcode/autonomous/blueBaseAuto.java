package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.PoseStorage;

public abstract class blueBaseAuto extends baseAuto{

    Pose2d dropoffPose = new Pose2d(45.5, 36.00, Math.toRadians(180.00));
    @Override
    public void end(){
        Pose2d poseEstimate = this.drive.getPoseEstimate();
        //rotate heading estimate by 180 degrees to prepare for driver control
        PoseStorage.savedPosition = new Pose2d(poseEstimate.getX(), poseEstimate.getY(), poseEstimate.getHeading() - Math.PI);

        this.manipulator.travelingPreset();
        sleep(500);
    }
}
