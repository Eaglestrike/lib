package org.usfirst.frc.team114.lib.auto.actions;

import org.usfirst.frc.team114.lib.auto.Action;
import org.usfirst.frc.team114.lib.auto.CompositeAction;

/**
 * An action that runs actions in sequence. This is useful if several actions need to be run where one only
 * one is possible (e.g. to set a sequence of actions in a {@link ParallelAction}). It should be used sparingly, and
 * a new action should be preferred if the sequence is likely to be reused.
 */
public class SerialAction extends CompositeAction {

    /**
     * Run each action in the list provided upon construction sequentially.
     */
    @Override
    public void run() {
        for (Action action: actions) {
             action.run();
        }
    }
}
