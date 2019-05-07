package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {
	private BlockingQueue<DeliveryVehicle> deliveryVehicleList;
	private ConcurrentLinkedQueue<Future> futureVehicles;
	/**
	 * creating a singleton of ResourcesHolder
	 */
	private static class SingletonHolder{
		private static ResourcesHolder instance=new ResourcesHolder();// ResourcesHolder singleton implementation
	}
	/**
	 * Constructor.
	 */
	private ResourcesHolder(){
		futureVehicles = new ConcurrentLinkedQueue<>();
		deliveryVehicleList=new LinkedBlockingQueue<>();
		}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> vehicle = new Future<>();//creating a new future that will be returned
		DeliveryVehicle v = deliveryVehicleList.poll();//trying to get a vehicle from vehicle list
		if(v == null){//in case the vehicle list is empty
			futureVehicles.add(vehicle);//adding a vehicle future that will be resolved later
		}
		else{
			vehicle.resolve(v);//if there is a vehicle, resolve vehicle future with it
		}
		return vehicle;
		}

	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		Future<DeliveryVehicle> ft = futureVehicles.poll();//checking if there is a future waiting to be resolved with the vehicle that just returned
		if(ft != null){//if there is a future waiting to be resolved
			ft.resolve(vehicle);//resolve it with vehicle
		}
		else {
			deliveryVehicleList.add(vehicle);//return the vehicle to delivery vehicle list
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for(DeliveryVehicle d:vehicles)
			deliveryVehicleList.add(d);
	}

}
