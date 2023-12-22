package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.Hand;
import org.firstinspires.ftc.teamcode.modules.Lift;

public class ManipulatorController {
    public Hand hand;


    public ServoWrapper wrist;
    public ServoWrapper shoulder;

    public Lift lift;

    public ManipulatorController(HardwareMap hardwareMap){
        this(hardwareMap, "shoulder", "wrist");
    }

    public ManipulatorController(HardwareMap hardwareMap, String shoulderName, String wristName ){
        this.hand = new Hand(hardwareMap);
        this.wrist = new ServoWrapper(hardwareMap, wristName, Math.toRadians(300.0), 0.0);
        this.shoulder = new ServoWrapper(hardwareMap, shoulderName, Math.toRadians(300.0), 0.0);
        this.lift = new Lift(hardwareMap);
    }

    public void log(Telemetry telemetry){
        telemetry.addData("Lift Height", this.lift.getTargetPosition());

    }

    public void travelingPreset(){
        this.lift.setTargetLength(0);
        this.wrist.setPosition(0.4416666);
        this.shoulder.setPosition(0.1288888);
    }
    public void floatingPickupPreset(){
        this.lift.setTargetLength(0);
        this.wrist.setPosition(0.16111111111);
        this.shoulder.setPosition(0.3083333333);
    }

    public void dropOffPreset(){
        this.lift.setTargetLength(15.0);
        this.wrist.setPosition(0.81944444);
        this.shoulder.setPosition(0.514444);
    }


    public void pickupPreset(){
        this.hand.open();
        this.lift.setTargetLength(0);
        this.wrist.setPosition(0.222777777);
        this.shoulder.setPosition(0.023888888);
    }
}
