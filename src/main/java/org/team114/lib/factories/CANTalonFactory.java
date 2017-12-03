package org.team114.lib.factories;

import edu.wpi.first.wpilibj.MotorSafety;
import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

/**
 * A factory class to create CANTalons with needed configuration settings.
 */
public class CANTalonFactory {

    private CANTalonFactory() {
        throw new AssertionError();
    }

    /**
     * Creates a new talon and configures it.
     * @param id the id for the new talon
     */
    public static CANTalon createConfiguredTalon(int id) {
        CANTalon talon = new CANTalon(id);
        configureTalon(talon);
        return talon;
    }

    /**
     * Configures an existing talon. This is provided in case we ever need
     * to reset a talon after its creation, and is also called by
     * {@link #createConfiguredTalon(int)}.
     *
     * @param talon the talon to be (re)configured
     */
    public static void configureTalon(CANTalon talon) {}


}
