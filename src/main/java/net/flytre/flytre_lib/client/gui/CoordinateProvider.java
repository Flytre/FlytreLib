package net.flytre.flytre_lib.client.gui;

/**
 * Simply any object which returns an x and y, useful for screens to implement for REI
 * exclusion zones
 */
public interface CoordinateProvider {

    int getX();

    int getY();
}
