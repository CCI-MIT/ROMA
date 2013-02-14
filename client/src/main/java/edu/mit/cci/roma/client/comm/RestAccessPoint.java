package edu.mit.cci.roma.client.comm;

import java.net.InetAddress;

/**
 * @author: jintrone
 * @date: May 19, 2010
 */
public interface RestAccessPoint {

     public String create(String context,  String... params);
}
