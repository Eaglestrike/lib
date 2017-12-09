package org.team114.lib.auto.actions;

import org.junit.*;
import org.mockito.*;

import org.team114.lib.auto.Action;

public class SerialActionTest {

    @Test
    public void testSerialRun() {
        Action spy1 = Mockito.mock(Action.class);
        Action spy2 = Mockito.mock(Action.class);
        Action spy3 = Mockito.mock(Action.class);
        InOrder inOrder = Mockito.inOrder(spy1, spy2, spy3);

        Action serial = new SerialAction(spy1, spy2, spy3);
        serial.run();

        inOrder.verify(spy1).run();
        inOrder.verify(spy2).run();
        inOrder.verify(spy3).run();

        Mockito.verifyNoMoreInteractions(spy1, spy2, spy3);
    }
}
