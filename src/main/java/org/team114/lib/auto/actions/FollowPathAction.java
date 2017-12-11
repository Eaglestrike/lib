package org.team114.lib.auto.actions;

import org.team114.lib.auto.Action;
import org.team114.lib.pathgenerator.HermiteWaypointSpline;

public class FollowPathAction extends Action {

    private HermiteWaypointSpline path;


    public FollowPathAction(HermiteWaypointSpline path) {
        this.path = path;
    }
    
    public void setPath(HermiteWaypointSpline path) {
        this.path = path;
    }


    @Override
    public void run() {

        /*
         * Just an idea for tank drive robots
         */
    }

}
