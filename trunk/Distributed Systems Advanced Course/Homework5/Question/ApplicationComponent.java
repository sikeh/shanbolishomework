package assignment5.components;

import org.apache.log4j.Logger;

import tbn.api.Component;
import tbn.api.HandlerNotSubscribedException;
import tbn.api.NoSuchMethodException;
import tbn.timer.TimerHandler;
import tbn.timer.events.TimerExpiredEvent;
import assignment4.events.ApplicationEvent;
import assignment5.events.TrustLeaderEvent;
import assignment5.events.UcDecideEvent;
import assignment5.events.UcProposeEvent;

public class ApplicationComponent {

	private Component component;

	private TimerHandler timerHandler;

	private static Logger log = Logger.getLogger(ApplicationComponent.class);

	private int lastOp;

	private String[] ops;

	public ApplicationComponent(Component component) {
		this.component = component;
		this.timerHandler = new TimerHandler(component);
	}

	public void handleApplicationEvent(ApplicationEvent event) {
		ops = event.getOps();
		lastOp = -1;
		doNextOp();
	}

	public void handleTrustLeaderEvent(TrustLeaderEvent event) {
		log.info("LEADER IS " + event.getLeader());
	}
	
	public void handleUcDecideEvent(UcDecideEvent event) {
		log.info("DECIDED(" + event.getInstance() + ")=" + event.getValue());
		doNextOp();
	}

	public void handleNextOp(TimerExpiredEvent event) {
		log.info("DONE WAITING");
		doNextOp();
	}

	private void doNextOp() {
		lastOp++;

		if (lastOp == ops.length) {
			log.info("DONE ALL OPS");
			System.exit(0);
		}
		String op = ops[lastOp];

		if (op.charAt(0) == 'P') {
			try {
				String args[] = op.substring(1).split("-");

				int instance = Integer.parseInt(args[0]);
				String value = args[1];

				log.info("PROPOSE(" + instance + "-" + value + ")");
				component.raiseEvent(new UcProposeEvent(instance, value));
			} catch (Exception e) {
				log.info("BAD OPEARTION: " + op);
				e.printStackTrace();
			}
		} else if (op.charAt(0) == 'D') {
			int d = Integer.parseInt(op.substring(1));
			log.info("WAITING " + d);
			try {
				timerHandler.startTimer(new TimerExpiredEvent(),
						"handleNextOp", d);
			} catch (HandlerNotSubscribedException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} else {
			log.info("BAD OPEARTION: " + op);
		}
	}
}
