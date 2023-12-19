package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.PoseStorage;

public abstract class blueBaseAuto extends baseAuto{
    @Override
    public void end(){
        Pose2d poseEstimate = this.drive.getPoseEstimate();
        //rotate heading estimate by 180 degrees to prepare for driver control
        PoseStorage.savedPosition = new Pose2d(poseEstimate.getX(), poseEstimate.getY(), poseEstimate.getHeading() - Math.PI);
    }
}
