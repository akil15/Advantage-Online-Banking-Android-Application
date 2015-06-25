package com.hp.advantage.utils;

@SuppressWarnings("serial")
public class AMBException extends RuntimeException {

    public AMBException() { super(); }
    public AMBException(String s) { super(s); }
    public AMBException(String s, Throwable throwable) { super(s, throwable); }
    public AMBException(Throwable throwable) { super(throwable); }

}
