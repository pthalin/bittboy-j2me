package de.avetana.bluetooth.sdp;

import javax.bluetooth.ServiceRecord;

import de.avetana.bluetooth.connection.JSR82URL;

public interface RecordOwner {

	public long getServiceHandle();

	public void setServiceRecord(ServiceRecord a_record) throws Exception;

	public ServiceRecord getServiceRecord();

	public boolean isNotifierClosed();

	public boolean isServiceRegistered();

	public JSR82URL getConnectionURL();

}
