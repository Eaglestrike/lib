package org.team114.lib.auto.actions;

import org.junit.*;

public class RunnerActionTest {

    @Test(expected = AssertionError.class)
    public void testRun() {
        RunnerAction run = new RunnerAction(() -> {
            throw new AssertionError();
        });
        run.run();
    }



}
