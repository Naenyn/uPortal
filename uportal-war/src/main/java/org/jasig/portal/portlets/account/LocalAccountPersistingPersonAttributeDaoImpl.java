package org.jasig.portal.portlets.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jasig.portal.persondir.ILocalAccountDao;
import org.jasig.portal.persondir.ILocalAccountPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localAccountPersistingPersonAttributeDaoImpl")
public class LocalAccountPersistingPersonAttributeDaoImpl implements
		IPersistingPersonAttributeDAO {

    private ILocalAccountDao accountDao;

    @Autowired
    public void setLocalAccountDao(ILocalAccountDao accountDao) {
        this.accountDao = accountDao;
    }
    
	@Override
	public void updateAttributes(String username,
			Map<String, List<Object>> attributes) {
		
        ILocalAccountPerson account;

        account = accountDao.getPerson(username);
        if (account == null) {
            account = accountDao.createPerson(username);
        }
        
        Map<String, List<String>> attributeStringMap = new HashMap<String, List<String>>();
        
        for (String key : attributes.keySet()) {
        	List<String> attributeStrings = new ArrayList<String>();
        	for (Object attribute : attributes.get(key)) {
        		attributeStrings.add(attribute != null ? attribute.toString() : null);
        	}
        	attributeStringMap.put(key, attributeStrings);
        }
        
        account.setAttributes(attributeStringMap);
	}

}
