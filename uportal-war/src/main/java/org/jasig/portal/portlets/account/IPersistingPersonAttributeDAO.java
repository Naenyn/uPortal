package org.jasig.portal.portlets.account;

import java.util.List;
import java.util.Map;

public interface IPersistingPersonAttributeDAO {

	public void updateAttributes(String username, Map<String, List<Object>> attributes);
	
}
