package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.autonomous.vision.AutoVisionProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Rect;


@Autonomous(preselectTeleOp = "drivermode")
public class blueLeft extends blueBaseAuto {

    AutoVisionProcessor visionProcessor;
    VisionPortal visionPortal;



    @Override
    public void initialize() {
        this.menu.addMenuObject("Park", new TelemetryMenu.Options(new String[]{"Left", "Right"}));

        this.visionProcessor = new AutoVisionProcessor(
                new double[]{0, 0, 230, 255},
                new Rect[]{
                        new Rect(0, 240, 280, 240),
                        new Rect(460, 240, 180, 190)
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
                    drive.setPoseEstimate(new Pose2d(14.00, 61.75, Math.toRadians(90.00)));
                    return drive.trajectorySequenceBuilder(new Pose2d(14.00, 61.75, Math.toRadians(90.00)))
                            .setReversed(true)
                            .lineTo(new Vector2d(19.00, 56.00))
                            .waitSeconds(0.25)
                            .build();
                },
                () -> {
                    visionPortal.close();

                    telemetry.addData("zone", visionProcessor.zone);
                    telemetry.update();

                    switch (visionProcessor.zone) {
                        case 0:
                            return drive.trajectorySequenceBuilder(new Pose2d(20.00, 56.00, Math.toRadians(90.00)))
                                    .setReversed(true)
                                    .lineTo(new Vector2d(28.0, 43.0))
                                    .setReversed(false)
                                    .build();

                        case 1:
                            return drive.trajectorySequenceBuilder(new Pose2d(20.00, 56.00, Math.toRadians(90.00)))
                                    .setReversed(true)
                                    .lineTo(new Vector2d(20.85, 33.0))
                                    .setReversed(false)
                                    .build();

                        default: //if in third zone
                            return drive.trajectorySequenceBuilder(new Pose2d(20.00, 56.00, Math.toRadians(90.00)))
                                    .setReversed(true)
                                    .splineToLinearHeading(new Pose2d(9.0, 30.5, Math.toRadians(0.00)), Math.toRadians(180.00))
                                    .setReversed(false)
                                    .build();
                    }
                },
                () -> {
                    double ypos;
                    switch (this.visionProcessor.zone){
                        case 0:
                            ypos = 44.0;
                            break;
                        case 1:
                            ypos = 37.0;
                            break;
                        default:
                            ypos = 32.0;
                            break;
                    }

                    this.finger.up();
                    return drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                            .waitSeconds(1.0)
                            .UNSTABLE_addDisplacementMarkerOffset(24.0, () -> this.manipulator.setConfiguration(this.dropoffPreset))
                            .splineToLinearHeading(new Pose2d(52.5, ypos, Math.toRadians(180.00)), Math.toRadians(-28.83))
                            .setReversed(false)
                            .UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                                this.manipulator.hand.open();
                            })
                            .waitSeconds(0.35)
                            .UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                                this.manipulator.wrist.setAngle(this.manipulator.wrist.getTargetAngle() + Math.toRadians(10.0));
                            })
                            .waitSeconds(1.0)
                            .lineToConstantHeading(new Vector2d(45.0, ypos))
                            .build();
                },
                () -> {
                    this.manipulator.travelingPreset();

                    switch(this.settings.get("Park")){
                        case "Right":
                            return drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                                    .splineToLinearHeading(new Pose2d(56, 12.0, Math.toRadians(180.00)), Math.toRadians(0.00))
                                    .build();
                        case "Left":
                            return drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                                    .splineToLinearHeading(new Pose2d(56.0, 61.0, Math.toRadians(180.00)), Math.toRadians(0.00))
                                    .build();
                    }
                    return null;
                }
        };
    }

    @Override
    public void tick(double deltaTime) {
    }
}
