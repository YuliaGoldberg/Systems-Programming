package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.GetVehicle;
import bgu.spl.mics.application.messages.ReturnVehicle;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.LinkedList;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private  ResourcesHolder resourcesHolder;
	private LinkedList<Future<DeliveryVehicle>> futureList;
	/**
	 * Constructor.
	 */
	public ResourceService(String name) {
		super(name);
		resourcesHolder = ResourcesHolder.getInstance();
		futureList = new LinkedList<>();
	}
	@Override
	protected void initialize() {

		this.subscribeBroadcast(Terminate.class, br-> {
				terminate();
		for (Future<DeliveryVehicle> f : futureList)//if there are Events that are waiting for a vehicle
			if(f != null)
				f.resolve(null);
		});
		this.subscribeEvent(GetVehicle.class, ev->{
			Future<DeliveryVehicle> vehicle = resourcesHolder.acquireVehicle();
			if (vehicle != null && !vehicle.isDone())//this means that we are in the waiting list for a vehicle
				futureList.add(vehicle);//add to the waiting list
			complete(ev, vehicle);
		});

		this.subscribeEvent(ReturnVehicle.class, ev->{
			resourcesHolder.releaseVehicle(ev.getVehicle());
			complete(ev, "Success");
		});
		BookStoreRunner.countUp.getAndIncrement();
	}

}
