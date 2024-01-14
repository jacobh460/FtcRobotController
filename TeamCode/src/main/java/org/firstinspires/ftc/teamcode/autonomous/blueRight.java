package org.firstinspires.ftc.teamcode.autonomous;


import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.autonomous.vision.AutoVisionProcessor;
import org.firstinspires.ftc.teamcode.modules.SpinTake;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Rect;

@Autonomous
public class blueRight extends blueBaseAuto {

    private AutoVisionProcessor visionProcessor;
    private VisionPortal visionPortal;

    private SpinTake spinTake;

    @Override
    public void initialize() {

        this.spinTake = new SpinTake(this.hardwareMap);
        this.spinTake.disable();

        this.menu.addMenuObject("Time Delay", new TelemetryMenu.Options(new String[]{"Off", "On"}));
        this.menu.addMenuObject("Truss", new TelemetryMenu.Options(new String[]{"Side", "Middle"}));
        this.menu.addMenuObject("Park", new TelemetryMenu.Options(new String[]{"Right", "Left"}));
        this.menu.addMenuObject("Drop Off", new TelemetryMenu.Options(new String[]{"True", "False"}));

        this.visionProcessor = new AutoVisionProcessor(
                new double[]{0, 0, 230, 255},
                new Rect[]{
                        new Rect(640 - 280, 240, 280, 240),//right zone
                        new Rect(10, 240, 180, 190)//middle zone
                },
                20, 100, 50, 255, 255, 0.3
        );

        this.visionPortal = new VisionPortal.Builder()
                .setCamera(this.hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(this.visionProcessor)
                .build();


        super.manipulator.floatingPickupPreset();
        super.flow = new step[]{
                () -> {
                    drive.setPoseEstimate(new Pose2d(-38.5, 61.75, Math.toRadians(90.00)));
                    return drive.trajectorySequenceBuilder(new Pose2d(-38.5, 61.75, Math.toRadians(90.00)))
                            .lineTo(new Vector2d(-43.0, 56.0))
                            .waitSeconds(0.25)
                            .build();
                },
                () -> {
                    this.visionPortal.close();

                    switch (this.visionProcessor.zone) {
                        case 0:
                            //drive to rightmost drop off spot
                            return drive.trajectorySequenceBuilder(new Pose2d(-43.0, 56.0, Math.toRadians(90.0)))
                                    .lineToConstantHeading(new Vector2d(-41.0, 42.0))
                                    .build();
                        case 1:

                            if (this.settings.get("Truss") == "Middle"){
                                //plow through middle spot
                                this.spinTake.enable();
                                this.spinTake.setVelocity(-500.0);
                                return drive.trajectorySequenceBuilder(new Pose2d(-43.00, 56.00, Math.toRadians(90.00)))
                                        .setReversed(true)
                                        .lineToSplineHeading(new Pose2d(-38.00, 17.0, Math.toRadians(270.0) + 1e-6))
                                        .setReversed(false)
                                        .build();
                            }

                            //drive up to middle spot
                            return drive.trajectorySequenceBuilder(new Pose2d(-43.00, 56.00, Math.toRadians(90.00)))
                                .setReversed(true)
                                .lineTo(new Vector2d(-34, 33))
                                .build();

                        default:
                            //drive to left spot
                            return drive.trajectorySequenceBuilder(new Pose2d(-43.0, 56.0, Math.toRadians(90.0)))
                                    .setReversed(true)
                                    .splineToSplineHeading(new Pose2d(-32.0, 37.0, Math.toRadians(180.0)), Math.toRadians(0.0))
                                    .setReversed(false)
                                    .build();
                    }
                },
                () -> {
                    this.finger.up();
                    TrajectorySequenceBuilder builder = this.drive.trajectorySequenceBuilder(this.drive.getPoseEstimate());

                    if (this.settings.get("Time Delay") == "On")
                        builder.waitSeconds(10.0);

                    double ypos;
                    switch (this.visionProcessor.zone) {
                        case 0:
                            ypos = 36.0;
                            break;
                        case 1:
                            ypos = 36.0;
                            break;
                        default:
                            ypos = 36.0;
                            break;
                    }


                    switch (this.settings.get("Truss")) {
                        case "Side":
                            this.spinTake.disable();
                            builder.splineToLinearHeading(new Pose2d(-33.71, 60.00, Math.toRadians(180.00)), Math.toRadians(0.00))//get ready to drive under truss
                                    .setReversed(true)
                                    .lineToConstantHeading(new Vector2d(30, 60))//drive under truss
                                    .UNSTABLE_addDisplacementMarkerOffset(0.0, () -> {
                                        this.manipulator.setConfiguration(this.dropoffPreset);
                                    })
                                    .splineToConstantHeading(new Vector2d(52.5, ypos), Math.toRadians(0.00))//drive to front of backdrop
                                    .setReversed(false);
                            break;

                        default: //middle or something went wrong
                            builder.splineToConstantHeading(new Vector2d(-31.38, 12.00), Math.toRadians(0.0))
                                    .setReversed(true)
                                    .lineToLinearHeading(new Pose2d(new Vector2d(32.40, 12.00), Math.toRadians(180.0)))
                                    .UNSTABLE_addDisplacementMarkerOffset(0.0, () -> {
                                        this.manipulator.setConfiguration(this.dropoffPreset);
                                    })
                                    .splineToConstantHeading(new Vector2d(52.5, ypos), Math.toRadians(0.0))
                                    .setReversed(false);
                            break;
                    }
                    return builder.build();
                },
                () -> {
                    this.spinTake.disable();

                    TrajectorySequenceBuilder builder = this.drive.trajectorySequenceBuilder(this.drive.getPoseEstimate());

                    //the next few calls just drop the pixel
                    builder.UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                        this.manipulator.hand.open();
                    });
                    builder.waitSeconds(.35);
                    builder.UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                        this.manipulator.wrist.setAngle(this.manipulator.wrist.getTargetAngle() + Math.toRadians(10.0));
                    });
                    builder.waitSeconds(1.0);
                    builder.UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                        this.manipulator.travelingPreset();
                    });

                    switch(this.settings.get("Park")){
                        case "Right":
                            //park to right of backdrop
                            builder.splineToLinearHeading(new Pose2d(56.28, 11.14, Math.toRadians(180.00)), Math.toRadians(0.00));
                            break;
                        case "Left":
                            //park to left of backdrop
                            builder.splineToLinearHeading(new Pose2d(52.78, 59.48, Math.toRadians(180.00)), Math.toRadians(0.00));
                            break;
                    }

                    return builder.build();
                }
        };

    }

    @Override
    public void tick(double deltaTime) {

    }


}
