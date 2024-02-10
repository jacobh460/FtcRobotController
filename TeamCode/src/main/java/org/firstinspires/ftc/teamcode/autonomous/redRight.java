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
public class redRight extends redBaseAuto{

    VisionPortal visionPortal;
    AutoVisionProcessor visionProcessor;

    @Override
    public void initialize(){
        this.menu.addMenuObject("Park", new TelemetryMenu.Options(new String[]{"Left", "Right"}));

        this.visionProcessor = new AutoVisionProcessor(
                new double[]{255, 0, 0, 255},
                new Rect[]{
                        new Rect(640 - 280, 240, 280, 240),//right zone
                        new Rect(0, 240, 190, 190)//middle zone
                },
                20, 100, 80, 255, 255, 0.3
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
                    this.drive.setPoseEstimate(new Pose2d( 14.00, -63.50, Math.toRadians(-90.0)));
                    return this.drive.trajectorySequenceBuilder(new Pose2d( 14.00, -63.50, Math.toRadians(-90.0)))
                            .setReversed(true)
                            .lineTo(new Vector2d(18.00, -60.00))
                            .waitSeconds(0.25)
                            .build();
                },
                () -> {
                    visionPortal.close();

                    telemetry.addData("zone", visionProcessor.zone);
                    telemetry.update();

                    switch (visionProcessor.zone) {
                        case 0://right
                            return drive.trajectorySequenceBuilder(new Pose2d(20.00, -56.00, Math.toRadians(-90.00)))
                                    .setReversed(true)
                                    .lineTo(new Vector2d(16.0, -43.0))
                                    .setReversed(false)
                                    .build();

                        case 1://mid
                            return drive.trajectorySequenceBuilder(new Pose2d(20.00, -56.00, Math.toRadians(-90.00)))
                                    .setReversed(true)
                                    .lineTo(new Vector2d(8.85, -34.0))
                                    .setReversed(false)
                                    .build();

                        default: //if in left zone
                            return drive.trajectorySequenceBuilder(new Pose2d(20.00, -56.00, Math.toRadians(-90.00)))
                                    .setReversed(true)
                                    .splineToLinearHeading(new Pose2d(7.0, -37.25, Math.toRadians(0.00)), Math.toRadians(-180.00))
                                    .setReversed(false)
                                    .build();
                    }
                },
                () -> {
                    double ypos;
                    switch (this.visionProcessor.zone){
                        case 0:
                            ypos = -44.0;//right
                            break;
                        case 1:
                            ypos = -37.0;//middle
                            break;
                        default:
                            ypos = -30.5;//left
                            break;
                    }

                    this.finger.up();
                    return drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                            .waitSeconds(1.0)
                            .UNSTABLE_addDisplacementMarkerOffset(24.0, () -> this.manipulator.setConfiguration(this.dropoffPreset))
                            .splineToSplineHeading(new Pose2d(51.5, ypos, Math.toRadians(180.00)), Math.toRadians(-28.83))
                            .setReversed(false)
                            .UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                                this.manipulator.hand.open();
                            })
                            .waitSeconds(0.35)
                            .UNSTABLE_addTemporalMarkerOffset(0.0, () -> {
                                this.manipulator.wrist.setAngle(this.manipulator.wrist.getTargetAngle() + Math.toRadians(10.0));
                            })
                            .waitSeconds(1.0)
                            .lineToConstantHeading(new Vector2d(42.0, ypos))
                            .build();
                },
                () -> {
                    this.manipulator.travelingPreset();

                    switch(this.settings.get("Park")){
                        case "Left":
                            return drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                                    .splineToLinearHeading(new Pose2d(56, -11.0, Math.toRadians(180.00)), Math.toRadians(0.00))
                                    .build();
                        case "Right":
                            return drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                                    .splineToLinearHeading(new Pose2d(56.0, -61.0, Math.toRadians(180.00)), Math.toRadians(0.00))
                                    .build();
                    }
                    return null;
                }
        };
    }

    @Override
    public void tick(double deltaTime){

    }
}
