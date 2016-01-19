package me.smbduknow.transport;

import java.io.Serializable;

public class Route implements Serializable, Comparable<Route> {

    public String id;
    public String label;
    public String type;
    public String typeLabel;

    @Override
    public int compareTo(Route another) {
        return id.compareTo(another.id);
    }
}
