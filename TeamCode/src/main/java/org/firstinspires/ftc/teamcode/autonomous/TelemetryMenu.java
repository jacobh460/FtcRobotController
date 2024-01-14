package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.Hashtable;

public class TelemetryMenu {
    Gamepad lastGamepad = new Gamepad();

    private int currentIndex = 0;
    public static class Options{
        public int index = 0;
        private String[] options;
        public Options(String[] options){
            this.options = options;
        }

        public String currentOption(){
            return this.options[this.index];
        }

        public void cycleRight(){
            this.index = Math.min(this.index + 1, this.options.length - 1);
        }

        public void cycleLeft(){
            this.index = Math.max(this.index - 1, 0);
        }
    }

    private class MenuOption{

        public String name;
        public Options options;
        public MenuOption(String settingName, Options options){
            this.name = settingName;
            this.options = options;
        }
    }
    private ArrayList<MenuOption> menuObjects = new ArrayList<MenuOption>();

    public TelemetryMenu addMenuObject(String name, Options options){
        menuObjects.add(new MenuOption(name, options));
        return this;
    }

    public void update(Gamepad gamepad, Telemetry telemetry){

        if (gamepad.dpad_down && !lastGamepad.dpad_down) this.currentIndex = Math.min(this.currentIndex + 1, this.menuObjects.size() - 1);
        if (gamepad.dpad_up && !lastGamepad.dpad_up) this.currentIndex = Math.max(this.currentIndex - 1, 0);
        if (gamepad.dpad_right && !lastGamepad.dpad_right) this.menuObjects.get(this.currentIndex).options.cycleRight();
        if (gamepad.dpad_left && !lastGamepad.dpad_left) this.menuObjects.get(this.currentIndex).options.cycleLeft();

        for (int i = 0; i < this.menuObjects.size(); ++i){
            telemetry.addData((this.currentIndex == i ? "> " : "  ") + this.menuObjects.get(i).name, this.menuObjects.get(i).options.currentOption());
        }
        telemetry.update();


        this.lastGamepad.copy(gamepad);
    }

    public Hashtable<String, String> save(){
        Hashtable<String, String> res = new Hashtable<String, String>();
        for (int i = 0; i < this.menuObjects.size(); ++i){
            res.put(this.menuObjects.get(i).name, this.menuObjects.get(i).options.currentOption());
        }
        return res;
    }
}
