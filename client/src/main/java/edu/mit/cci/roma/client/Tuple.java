package edu.mit.cci.roma.client;

import java.util.List;


/**
 * Simple array of values
 *
 * @author jintrone
 *
 */
public interface Tuple {

	public String[] getValues();
	public void setValues(String[] vals);
    public TupleStatus getStatus(int index);
    public List<TupleStatus> getAllStatuses();
    

}