package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(51.92649965597488, 30, Math.toRadians(192.9526719807494), Math.toRadians(60), 18)
                .setDimensions(18.0,17.0)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(9.0, 30.5, Math.toRadians(0.00)))
                                .splineToLinearHeading(new Pose2d(52.5, 46, Math.toRadians(180.00)), Math.toRadians(-28.83))
                                .build()











                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}