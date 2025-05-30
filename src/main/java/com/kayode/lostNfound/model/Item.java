/**
 * 
 */
package com.kayode.lostNfound.model;


import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.kayode.lostNfound.model.AbstractEntity;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Kayode Ojo
 */
@Entity
public class Item extends AbstractEntity {

@Temporal(TemporalType.TIMESTAMP)
private Date createdDate;
@Temporal(TemporalType.TIMESTAMP)
private Date lastModifiedDate;
private String name;
private String category;
private String location;
private Date dateReported;
private Date dateClaimed;
@Enumerated(EnumType.STRING)
private ItemType itemType;
@Enumerated(EnumType.STRING)
private ItemStatus itemStatus;


/**
 * @return the itemType
 */
public ItemType getItemType(){
	return itemType;
}
/**
 * @param itemType the itemType to set
 */
public void setItemType(ItemType itemType) {
	this.itemType = itemType;
}/**
 * @return the itemStatus
 */
public ItemStatus getItemStatus(){
	return itemStatus;
}
/**
 * @param itemStatus the itemStatus to set
 */
public void setItemStatus(ItemStatus itemStatus) {
	this.itemStatus = itemStatus;
}/**
 * @return the name
 */
public String getName(){
	return name;
}
/**
 * @param name the name to set
 */
public void setName(String name) {
	this.name = name;
}/**
 * @return the dateReported
 */
public Date getDateReported(){
	return dateReported;
}
/**
 * @param dateReported the dateReported to set
 */
public void setDateReported(Date dateReported) {
	this.dateReported = dateReported;
}/**
 * @return the location
 */
public String getLocation(){
	return location;
}
/**
 * @param location the location to set
 */
public void setLocation(String location) {
	this.location = location;
}/**
 * @return the dateClaimed
 */
public Date getDateClaimed(){
	return dateClaimed;
}
/**
 * @param dateClaimed the dateClaimed to set
 */
public void setDateClaimed(Date dateClaimed) {
	this.dateClaimed = dateClaimed;
}/**
 * @return the category
 */
public String getCategory(){
	return category;
}
/**
 * @param category the category to set
 */
public void setCategory(String category) {
	this.category = category;
}
@PrePersist
private void onCreate() {
	createdDate = new Date();
}
@PreUpdate
private void onUpdate() {
	lastModifiedDate = new Date();
}

}