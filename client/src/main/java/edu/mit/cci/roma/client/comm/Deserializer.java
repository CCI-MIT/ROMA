package edu.mit.cci.roma.client.comm;

import java.io.Reader;

/**
 * @author: jintrone
 * @date: May 19, 2010
 */
public interface Deserializer {
    public Object deserialize(Reader stream);
}
