package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import java.util.Locale;


@TeleOp(group = "test")
public class test_colorSensor extends LinearOpMode {

    @Override
    public void runOpMode(){
        ColorSensor right = hardwareMap.get(ColorSensor.class, "rightSense");
        right.enableLed(true);

        waitForStart();

        telemetry.setMsTransmissionInterval(40);

        while (opModeIsActive()){
            int argb = right.argb();
            double alpha = ((argb >> 24) & 0xFF);
            double red = (argb >> 16) & 0xFF;
            double green = (argb >> 8) & 0xFF;
            double blue = argb & 0xFF;

            red = red/255.0 * alpha;
            green = green/255.0 * alpha;
            blue = blue/255.0 * alpha;

            telemetry.addData("COLOR RGB", String.format(Locale.ENGLISH, "%f, %f, %f", red, green, blue));

            telemetry.addData("COLOR ARGB", String.format(Locale.ENGLISH, "%d, %d, %d, %d", (argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF));

            //telemetry.addData("COLOR ARGB", String.format(Locale.ENGLISH, "%d, %d, %d, %d", right.alpha(), right.red(), right.green(), right.blue()));
            telemetry.update();
        }

        right.enableLed(false);
    }
}
