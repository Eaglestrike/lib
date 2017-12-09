package org.team114.lib.auto.actions;

import org.junit.*;
import org.mockito.*;

import static org.mockito.Mockito.times;

import org.team114.lib.auto.Action;

public class SerialActionTest {

    @Test
    public void testSerialRun() {
        Action spy = Mockito.mock(Action.class);
        Action serial = new SerialAction(spy, spy, spy);
        serial.run();
        Mockito.verify(spy, times(3)).run();
    }
}
