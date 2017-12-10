package org.team114.lib.auto.actions;

import org.junit.*;
import org.mockito.*;
import org.team114.lib.auto.Action;

import static org.mockito.Mockito.times;

public class RunnerActionTest {

    private Runnable runnable = Mockito.mock(Runnable.class);
    private Action action = new RunnerAction(runnable);

    @Test
    public void testRun() {
        action.run();
        Mockito.verify(runnable, times(1)).run();
    }

}
