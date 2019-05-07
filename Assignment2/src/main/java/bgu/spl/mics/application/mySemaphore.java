package bgu.spl.mics.application;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

public class mySemaphore extends Semaphore implements Serializable {
    /**
     * Constructor.
     */
    public mySemaphore(int permits){
        super(permits);
    }

    /**
     * decrease the amount of permits
     */
    public void decreasePermits(){
        this.reducePermits(1);
    }

}
