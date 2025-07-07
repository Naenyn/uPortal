package org.apache.pluto.container;

import java.io.Serializable;

public interface PortletPreference extends Serializable {
    String getName();
    String[] getValues();
    boolean isReadOnly();
}