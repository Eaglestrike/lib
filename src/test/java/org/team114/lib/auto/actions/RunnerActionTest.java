package org.team114.lib.auto.actions;

import org.junit.*;
import org.mockito.*;

import static org.mockito.Mockito.times;

public class RunnerActionTest {

    private Runnable runnable = Mockito.mock(Runnable.class);

    @Test
    public void testRun() {
        runnable.run();
        Mockito.verify(runnable, times(1)).run();
    }

}
