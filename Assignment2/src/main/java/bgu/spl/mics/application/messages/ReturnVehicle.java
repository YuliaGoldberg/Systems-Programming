package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;


public class ReturnVehicle implements Event<String> {
    private DeliveryVehicle vehicle;

    public ReturnVehicle(DeliveryVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }

}