package org.firstinspires.ftc.teamcode.modules;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ActionScheduler {

    private final PriorityQueue<element> todo;

    public ActionScheduler(){
        this.todo = new PriorityQueue<>(Comparator.comparingLong(element -> element.runTime));
    }

    public interface Action{
        public void run();
    }

    private static class element{

        public Action action;
        public long runTime;
        public element(Action action, long startTime, long waitTime){
            this.action = action;
            this.runTime = startTime + waitTime;
        }
    }


    public void tick(){

        double currentTime = System.nanoTime();

        while (this.todo.size() > 0){
            if (this.todo.peek().runTime > currentTime) break;

            this.todo.poll().action.run();
        }
    }


    /**
     *
     * @param action action to be run
     * @param waitTime time in ms from now when action should be run
     */
    public void addAction(Action action, long waitTime){
        this.todo.offer(new element(action, System.nanoTime(), waitTime * 1000000));
    }


}
