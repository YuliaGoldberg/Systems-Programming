package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	/**
	 * Constructor.
	 */
	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(Terminate.class, br-> terminate());

		this.subscribeEvent(DeliveryEvent.class, ev-> {
			Future<Future<DeliveryVehicle>> futureVehicle = this.sendEvent(new GetVehicle());//asking for vehicle from resourceService. maybe the vehicle is not resolved yet
			if (futureVehicle != null) {//non of the ResourceServices has subscribed to GetVehicle event or there is no vehicles in the ResourceHolder
				Future<DeliveryVehicle> vehicle=futureVehicle.get();
				if (vehicle != null) {//if there is no vehicles in the ResourceHolder
					DeliveryVehicle car = vehicle.get();
					if (car != null) {
						car.deliver(ev.getAddress(), ev.getDistance());//delivering the book to customer's house. deliver will sleep for the whole time it would take
						this.sendEvent(new ReturnVehicle(car));//returning the car to resourceService
						complete(ev, "success");
					} else
						complete(ev, null);
				} else
					complete(ev, null);
			}else
				complete(ev, null);
		});
		BookStoreRunner.countUp.getAndIncrement();
	}
}
