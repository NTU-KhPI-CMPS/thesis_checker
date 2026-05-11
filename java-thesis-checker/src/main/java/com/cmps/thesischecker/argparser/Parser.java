package com.cmps.thesischecker.argparser;

public interface Parser<VALUE_TYPE> {
    
    VALUE_TYPE parse(String[] args);
}
