package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailability implements Event<Integer> {
    private String bookName;

    public CheckAvailability(String bookName){
        this.bookName=bookName;
    }

    public String getBookName(){
        return  bookName;
    }
}
