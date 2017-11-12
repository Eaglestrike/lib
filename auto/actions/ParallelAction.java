package org.usfirst.frc.team114.lib.auto.actions;

import org.usfirst.frc.team114.lib.auto.ActionBase;
import org.usfirst.frc.team114.lib.auto.CompositeAction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * An action that executes several actions in parallel, each running in its own thread.
 */
public class ParallelAction extends CompositeAction {
    /**
     * An executor that is responsible for running the actions.
     */
    private ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Execute every action concurrently.
     */
    @Override
    public void run() {
        for (ActionBase action : actions) {
            executor.execute(action);
        }
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }
    }
}
