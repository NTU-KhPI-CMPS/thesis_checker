package com.cmps.thesischecker.argparcer;

public interface ArgumentParser<T> {

    T parse(String arg);
}
