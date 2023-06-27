package com.numbytes.maven.plugins;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

class SortedProperties extends Properties {

    public SortedProperties loadFromProperties(Properties props) {
        Enumeration keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            this.setProperty(key, props.getProperty(key));
        }
        return this;
    }

    public Enumeration keys() {
        Enumeration keysEnum = super.keys();
        Vector<String> keyList = new Vector<String>();
        while (keysEnum.hasMoreElements()) {
            keyList.add((String) keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }
}
