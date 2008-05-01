package sicssim.main;

import sicssim.config.SicsSimConfig;
import sicssim.coolstream.auxiliaryoverlay.UpdateOverlay;
import sicssim.scheduler.SicsSimB;

/**
 * The main method of simulator
 * 
 * @author Amir Payberah
 * @author Fatemeh Rahimian
 *
 */
public class Main {

	public static void main(String[] args) {
		SicsSimB sim = new SicsSimB(UpdateOverlay.class);
		sim.runTill(SicsSimConfig.SIM_TIME);
	}
}
