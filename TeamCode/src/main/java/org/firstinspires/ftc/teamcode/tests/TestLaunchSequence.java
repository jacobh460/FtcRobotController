package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.modules.AirplaneHolder;


@TeleOp(group="test")
public class TestLaunchSequence extends LinearOpMode {


    @Override
    public void runOpMode(){
        AirplaneHolder holder = new AirplaneHolder(this.hardwareMap, "airplaneHolder");

    }
}
