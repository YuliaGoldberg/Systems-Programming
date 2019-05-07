package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private int currentTick;
	/**
	 * Constructor.
	 */
	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed=speed;
		this.duration=duration;
		currentTick=1;
	}

	@Override
	protected void initialize() {
		Timer time=new Timer();
		TimerTask timerTask=new TimerTask() {
			@Override
			public void run() {
				if (currentTick < duration) {
					sendBroadcast(new TickBroadcast(currentTick, duration));
					currentTick++;
				} else {
					sendBroadcast(new Terminate());
					time.cancel();
					time.purge();
				}
			}

		};
		time.schedule(timerTask,speed,speed);

		this.subscribeBroadcast(Terminate.class, br-> terminate());

		BookStoreRunner.countUp.getAndIncrement();
	}
}
