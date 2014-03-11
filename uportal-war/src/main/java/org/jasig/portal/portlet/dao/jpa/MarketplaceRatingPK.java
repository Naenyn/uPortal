package org.jasig.portal.portlet.dao.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@SecondaryTables({
		@SecondaryTable(name="UP_PORTLET_DEF"),
		@SecondaryTable(name="UP_PERSON_DIR")
})
@Embeddable
public class MarketplaceRatingPK implements Serializable{

	private static final long serialVersionUID = -1294203685313115404L;

	@Column(name="USER_NAME", unique=false, nullable=true, insertable=true, updatable=true)
	protected String userName;

	@ManyToOne
	@Cascade({CascadeType.PERSIST})
	@JoinColumn(name="PORTLET_ID", referencedColumnName="PORTLET_DEF_ID", unique=false, nullable=true, insertable=true, updatable=true)
	protected PortletDefinitionImpl portletDefinition;

	/**
	 * Empty constructor is needed for Serializable
	 */
	public MarketplaceRatingPK(){
	};

    public MarketplaceRatingPK(String userName, PortletDefinitionImpl portletDefinition) {
        this.userName = userName;
        this.portletDefinition = portletDefinition;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PortletDefinitionImpl getPortletDefinition() {
		return portletDefinition;
	}

	public void setPortletDefinition(PortletDefinitionImpl portletID) {
		this.portletDefinition =  portletID;
	}

    @Override
    public boolean equals(Object obj){
        if (obj == null || !(obj instanceof MarketplaceRatingPK)){
            return false;
        }else if(obj == this){
            return true;
    	}
        MarketplaceRatingPK tempRating = (MarketplaceRatingPK)obj;
        return new EqualsBuilder().
            append(userName, tempRating.userName).
            append(portletDefinition, tempRating.portletDefinition).
            isEquals();
    }

	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            append(userName).
	            append(portletDefinition).
	            toHashCode();
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("User: ");
		builder.append(this.userName);
		builder.append("\n");
		builder.append("Portlet: ");
		builder.append(this.portletDefinition);
		return builder.toString();
	}
}
