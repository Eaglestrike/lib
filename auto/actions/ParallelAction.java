package org.usfirst.frc.team114.lib.auto.actions;

import org.usfirst.frc.team114.lib.auto.Action;
import org.usfirst.frc.team114.lib.auto.CompositeAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An action that executes several actions in parallel, each running in its own thread.
 */
public class ParallelAction extends CompositeAction {
    /**
     * An executor that is responsible for running the actions. The thread pool is cached
     * to create threads as needed. Threads will not be destroyed for 60 seconds, longer
     * than the autonomous phase, and the same thread pool is used for all ParallelActions
     * so overhead will be minimal.
     */
    private static ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Execute every action concurrently, each one running on a thread from the pool.
     */
    @Override
    public void run() {

        List<Future<Object>> futures = new ArrayList<>();

        for (Action action : actions) {
            futures.add(executor.submit(action, null));
        }

        for (Future<Object> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e ) {
                System.out.println("Exception in parallel execution: " + e.getMessage());
            }
        }
    }
}
