/**
 * 
 */
package com.kayode.lostNfound.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.subject.Subject;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kayode.lostNfound.constants.Constants;
import com.kayode.lostNfound.constants.QueryType;
import com.kayode.lostNfound.lazymodel.ItemLazyDataModel;
import com.kayode.lostNfound.model.Item;
//import com.kayode.lostNfound.model.Role;
//import com.kayode.lostNfound.model.User;
import com.kayode.lostNfound.service.ItemService;
//import com.kayode.lostNfound.service.UserService;

/**
 * @author AAfolayan
 *
 */
@Named("itemBean")
@ViewScoped
public class ItemBean implements Serializable {

    public static final String APP_BASE_NAME = Constants.APP_BASE_NAME;
	private static Logger LOG = LoggerFactory.getLogger(ItemBean.class);
	
	@Inject
	private ItemService itemService;
	private Item entry = new Item();
	private LazyDataModel<Item> lazyModel;
	
	private static final String ITEM_MGT_URL = APP_BASE_NAME + "/online/item/list.xhtml?faces-redirect=true";
	private static final String ITEM_CREATION_URL = APP_BASE_NAME + "/online/item/create.xhtml?faces-redirect=true";

	private List<Item> entries = new ArrayList<>();
																		
	

	@PostConstruct
	public void init() {
		LOG.info("ItemBean init!");
	}
	
	public void listItem() {
		LOG.info("listItem invoked!!!");
		try {
			//SecurityUtils.getSubject().checkRole("MDOPS");
			lazyModel = new ItemLazyDataModel(itemService, QueryType.GET_ALL_ITEM);
		} catch (Exception e) {
			Messages.addGlobalError("oops error encountered while fetching entries!");
			LOG.error("oops error encountered while fetching entries!", e.fillInStackTrace());
			e.printStackTrace(); //
		}
	}


	public void updateItem() {
		LOG.info("updateItem invoked!!!");
		try {
			//SecurityUtils.getSubject().checkRole("MDOPS");
			LOG.info("entry -> " + entry);
			itemService.updateItem(entry);
			Messages.addFlashGlobalInfo("Item update request processed successfully!");
			entry = new Item();
			Faces.redirect(ITEM_MGT_URL);
		} catch (Exception e) {
			Messages.addGlobalError("Item update request failed!");
			LOG.error("Item update request failed!", e.fillInStackTrace());
			e.printStackTrace(); // TODO: logger.
			// return null;
		}
	}
	
	
		public void createItem() {
		LOG.info("createItem invoked!!!");
		try {
			//SecurityUtils.getSubject().checkRole("MDOPS");
			LOG.info("entry -> " + entry);
			itemService.createItem(entry);
			Messages.addFlashGlobalInfo("Item creation request processed successfully!");
			entry = new Item();
			Faces.redirect(ITEM_MGT_URL);
		} catch (Exception e) {
			Messages.addGlobalError("Item creation failed!");
			LOG.error("Item creation failed!", e.fillInStackTrace());
			e.printStackTrace(); // TODO: logger.
			// return null;
		}
	}
	
	public void createNewItemView() throws IOException {
		LOG.info("createNewItemView invoked");
		this.entry=new Item();
	}
	
  public void createItemView() throws IOException {
		LOG.info("createItemView invoked");
		Faces.redirect(ITEM_CREATION_URL);
	}
	
		public void displayItemDialog(Item e) {
		LOG.info("displayItemDialog invoked!");
		this.entry = e;
		LOG.info("entry selected:  id -> " + this.entry.getId());

	}
	
		public void prepare() {
		LOG.info("prepare method invoked!");
		Flash flash = Faces.getFlash();// FacesContext.getCurrentInstance().getExternalContext().getFlash();
		this.entry = (Item) flash.get("entry");
		LOG.info("selected Item retrieved >>> " + entry);
	}
	
	
	
	
		/**
	 * @return the entry
	 */
	public Item getEntry() {
		return entry;
	}

	/**
	 * @param entry the entry to set
	 */
	public void setEntry(Item entry) {
		this.entry = entry;
	}
	
		/**
	 * @return the entries
	 */
	public List<Item> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(List<Item> entries) {
		this.entries = entries;
	}
	
		/**
	 * @return the lazyModel
	 */
	public LazyDataModel<Item> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<Item> lazyModel) {
		this.lazyModel = lazyModel;
	}
	
	

}
