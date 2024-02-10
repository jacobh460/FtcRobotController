package org.firstinspires.ftc.teamcode.Manipulator;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.modules.ServoWrapper;

public class ManipulatorController {
    public Hand hand;

    public static ManipulatorConfiguration[] presets = {
            new ManipulatorConfiguration(Lift.minLength, Math.toRadians(4.778149), Math.toRadians(-96.65054)), //pickup
            new ManipulatorConfiguration(Lift.minLength, Math.toRadians(67.7262597), Math.toRadians(-96.65054)), //transition between pickup and traveling
            new ManipulatorConfiguration(Lift.minLength, Math.toRadians(35.5306599), Math.toRadians(-35.0787366)), //traveling
            new ManipulatorConfiguration(17.0, Math.toRadians(136.1411), Math.toRadians(87.41127)) //dropoff
    };


    public ServoWrapper wrist;
    public ServoWrapper shoulder;

    public Lift lift;

    public ManipulatorController(HardwareMap hardwareMap){
        this(hardwareMap, "shoulder", "wrist");
    }

    public ManipulatorController(HardwareMap hardwareMap, String shoulderName, String wristName){
        this.hand = new Hand(hardwareMap);
        this.wrist = new ServoWrapper(hardwareMap, wristName, Math.toRadians(300.0), Math.toRadians(155.56536759), 98.142681/90.0);
        this.shoulder = new ServoWrapper(hardwareMap, shoulderName, Math.toRadians(180.0), Math.toRadians(22.553865 - 0.5614 + 1.2), 1.0);
        this.shoulder.servo.setDirection(Servo.Direction.REVERSE);//idk axon servo needs to be reversed for some reason
        this.lift = new Lift(hardwareMap);
    }

    public void log(Telemetry telemetry){
        telemetry.addData("Lift Height", this.lift.getTargetLength());

    }

    public void setConfiguration(ManipulatorConfiguration configuration){
        this.lift.setTargetLength(configuration.L1);
        this.shoulder.setAngle(configuration.thetaA);
        this.wrist.setAngle(configuration.thetaB);
    }


    public void travelingPreset(){
        this.setConfiguration(ManipulatorController.presets[2]);
    }

    public void floatingPickupPreset(){
        this.setConfiguration(ManipulatorController.presets[1]);
    }

    public void dropOffPreset(){
        this.setConfiguration(ManipulatorController.presets[3]);
    }

    public void pickupPreset(){
        this.setConfiguration(ManipulatorController.presets[0]);
    }



}
