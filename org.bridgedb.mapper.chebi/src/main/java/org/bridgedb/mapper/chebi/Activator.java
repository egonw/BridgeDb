package org.bridgedb.mapper.chebi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
	}

	@Override
	public void stop(BundleContext context) throws Exception {}

}
