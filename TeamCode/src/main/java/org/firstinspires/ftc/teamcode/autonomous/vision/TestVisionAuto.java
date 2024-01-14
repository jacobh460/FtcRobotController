package org.firstinspires.ftc.teamcode.autonomous.vision;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.opencv.core.Rect;


@Autonomous(group = "test")
public class TestVisionAuto extends LinearOpMode {

    @Override
    public void runOpMode(){
        double[] targetColor = {150, 0, 0, 255};//rgba
        Rect[] zones = {
                new Rect(0, 0, 320, 480),
                new Rect(320, 0, 320, 480)
        };

        AutoVisionProcessor processor = new AutoVisionProcessor(targetColor, zones, 30, 100, 100, 255, 255, 0.3);

        VisionPortal portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(processor)
                .build();


        waitForStart();
    }
}
