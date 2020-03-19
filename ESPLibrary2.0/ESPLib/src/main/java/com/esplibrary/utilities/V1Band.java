package com.esplibrary.utilities;

/**
 * Detection bands
 */
public enum V1Band {
    /**
     * X band
     */
    X("X"),
    /**
     * Ku band
     */
    Ku("Ku"),
    /**
     * K band
     */
    K("K"),
    /**
     * Ka band
     */
    Ka("Ka"),
    /**
     * Sub band that encompasses the lower third of the Ka band
     */
    Ka_Lo("Ka Lo"),
    /**
     * Sub band that encompasses the middle third of the Ka band
     */
    Ka_Mid("Ka Mid"),
    /**
     * Sub band that encompasses the upper third of the Ka band
     */
    Ka_Hi("Ka Hi"),
    /**
     * Pop band
     */
    POP("POP"),
    /**
     * Invalid band
     */
    No_Band("No Band");

    final String name;

    V1Band(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}