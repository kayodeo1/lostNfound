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
 * @author AAfolayan
 */
@Entity
public class Media extends AbstractEntity {

@Temporal(TemporalType.TIMESTAMP)
private Date createdDate;
@Temporal(TemporalType.TIMESTAMP)
private Date lastModifiedDate;
private Long itemID;
@Enumerated(EnumType.STRING)
private MediaType mediaType;
private String extension;
private String identifier;


/**
 * @return the itemID
 */
public Long getItemID(){
	return itemID;
}
/**
 * @param itemID the itemID to set
 */
public void setItemID(Long itemID) {
	this.itemID = itemID;
}/**
 * @return the identifier
 */
public String getIdentifier(){
	return identifier;
}
/**
 * @param identifier the identifier to set
 */
public void setIdentifier(String identifier) {
	this.identifier = identifier;
}/**
 * @return the extension
 */
public String getExtension(){
	return extension;
}
/**
 * @param extension the extension to set
 */
public void setExtension(String extension) {
	this.extension = extension;
}/**
 * @return the mediaType
 */
public MediaType getMediaType(){
	return mediaType;
}
/**
 * @param mediaType the mediaType to set
 */
public void setMediaType(MediaType mediaType) {
	this.mediaType = mediaType;
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