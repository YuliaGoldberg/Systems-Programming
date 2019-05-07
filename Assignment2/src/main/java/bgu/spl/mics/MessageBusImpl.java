package bgu.spl.mics;

import bgu.spl.mics.application.BookStoreRunner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Event,Future> eventToFuture;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> serviceList;
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers;
	private ConcurrentLinkedQueue<Class<? extends Event>> saveEvents; //all the events that a MS was subscribed to them
	private ConcurrentLinkedQueue<Class<? extends Broadcast>> saveBroadcasts; //all the broadcasts that a MS was subscribed to them

	/**
	 * creating a singleton of MessageBusImpl
	 */
	private static class SingletonHolder{
		private static MessageBusImpl instance=new MessageBusImpl();
	}
	/**
	 * Constructor.
	 */
	private MessageBusImpl(){
		eventToFuture = new ConcurrentHashMap<>();
		serviceList = new ConcurrentHashMap<>();
		eventSubscribers = new ConcurrentHashMap<> ();
		broadcastSubscribers = new ConcurrentHashMap<>();
		saveEvents = new ConcurrentLinkedQueue<>();
		saveBroadcasts = new ConcurrentLinkedQueue<>();

	}
	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;

	}

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		saveEvents.add(type);
		ConcurrentLinkedQueue eventQ = new ConcurrentLinkedQueue<>();// a queue of all MS which subscribed for a specific event
		this.eventSubscribers.putIfAbsent(type, eventQ); //if the event doesn't exist yet
		this.eventSubscribers.get(type).add(m);//push MC to a  exist Q that contains all the MC that subscribed for this event
	}


	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		saveBroadcasts.add(type);
		ConcurrentLinkedQueue broadcastQ = new ConcurrentLinkedQueue<>();// a queue of all MS which subscribed for a specific broadcast
		this.broadcastSubscribers.putIfAbsent(type, broadcastQ); //if the broadcast doesn't exist yet
		this.broadcastSubscribers.get(type).add(m);//push MC to a exist Q that contains all the MC that subscribed for this broadcast

	}
	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (eventSubscribers.get(e.getClass())) {
			eventToFuture.get(e).resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService m : broadcastSubscribers.get(b.getClass())) {//find all the MC that subscribed for this broadcast
					serviceList.get(m).offer(b);//add this broadcast (b) to the MC Q
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = null;
		ConcurrentLinkedQueue<MicroService> eventListOfQ=eventSubscribers.get(e.getClass());
		synchronized (eventListOfQ) {
			if (!eventListOfQ.isEmpty()) {
				MicroService microService = eventSubscribers.get(e.getClass()).poll(); //get one of the (MS) subscribers of Event e
				if (microService != null) {
						future = new Future<>();
						eventToFuture.put(e, future); // adding the event with its Future to eventToFuture hashMap
						if (!serviceList.isEmpty())
							serviceList.get(microService).add(e); // insert Event e to the queue of 'microService'
						eventSubscribers.get(e.getClass()).add(microService); // adding microService to the end of the Q
				}
			}
		}
		return future;
	}


	@Override
	public void register(MicroService m) {
		BlockingQueue newMC=new LinkedBlockingQueue();
		serviceList.put(m,newMC);
	}

	@Override
	public void unregister(MicroService m) {
		//removes all the references of MicroService m in all the event lists
		if (serviceList.containsKey(m)) {
				for (Class<? extends Event> event : saveEvents) {//for every event
					ConcurrentLinkedQueue<MicroService> Q = eventSubscribers.get(event);
					synchronized (Q) {
						Q.remove(m);
					}
				}
				//removes all the references of MicroService m in all the broadcast lists
				for (Class<? extends Broadcast> broadcast : saveBroadcasts) {//for every event
					ConcurrentLinkedQueue<MicroService> Q = broadcastSubscribers.get(broadcast);
						Q.remove(m);

				}

				BlockingQueue<Message> queue = serviceList.remove(m);//remove this microService from MessageBus
				for (Message message : queue) {
					if (message instanceof Event) {
						eventToFuture.get(message).resolve(null);
					}
				}

		}
		BookStoreRunner.countDown.getAndDecrement();
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!serviceList.containsKey(m)) // in case that the MS m was never registered
			throw new IllegalStateException("MicroService "+ m +" was never registered" );
		Message message = serviceList.get(m).take(); //waits until there is a message available and removes the first message from the queue
		return message;
	}
}
