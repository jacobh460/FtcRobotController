package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.PoseStorage;

public abstract class redBaseAuto extends baseAuto{

    @Override
    public void end(){

        Pose2d poseEstimate = this.drive.getPoseEstimate();
        PoseStorage.savedPosition = poseEstimate;
    }

}
