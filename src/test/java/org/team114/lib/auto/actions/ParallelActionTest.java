package org.team114.lib.auto.actions;

import org.junit.*;
import org.mockito.Mockito;

import org.team114.lib.auto.Action;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.times;

public class ParallelActionTest {

    private CountDownLatch latch = new CountDownLatch(3);
    private Runnable runnable = ()  -> {
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.print("Test interrupted: " + e.getMessage());
        }
    };

    private Action spy = Mockito.spy(new RunnerAction(runnable));
    private Action parallel = new ParallelAction(spy, spy, spy);

    @Test(timeout = 250)
    public void testConcurrentExecution() {
        parallel.run();

        Mockito.verify(spy, times(3)).run();
        Mockito.verifyNoMoreInteractions(spy);
    }
}

