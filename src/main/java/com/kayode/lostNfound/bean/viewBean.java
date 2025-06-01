package com.kayode.lostNfound.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kayode.lostNfound.constants.Constants;
import com.kayode.lostNfound.constants.QueryType;
import com.kayode.lostNfound.lazymodel.ItemLazyDataModel;
import com.kayode.lostNfound.model.Category;
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.ItemStatus;
import com.kayode.lostNfound.model.ItemType;
import com.kayode.lostNfound.model.Media;
import com.kayode.lostNfound.model.MediaType;
import com.kayode.lostNfound.service.ItemService;
import com.kayode.lostNfound.service.MediaService;

@Named("viewBean")
@ViewScoped
public class viewBean implements Serializable {
	public static final String APP_BASE_NAME = Constants.APP_BASE_NAME;
	private static final String VIEW_URL = APP_BASE_NAME + "/view2.xhtml?faces-redirect=true";

	@Inject
	private ItemService itemService;
	private Item entry = new Item();
	private LazyDataModel<Item> lazyModel;
	private LazyDataModel<Item> lazyModel1;
	private LazyDataModel<Item> lazyModel2;
	private static Logger LOG = LoggerFactory.getLogger(ItemBean.class);
	private List<Category> categories;
	private UploadedFile file;
	private UploadedFile file2;
	private String fileName;
	private String fileExtension;
	private Path previewPath;
	private Media entryMedia;
	@Inject
	MediaService mediaService;

	@PostConstruct
	public void init() {
		System.out.println("view bean invoked!!!");
		setCategories(Arrays.asList(Category.values()));

	}

	public void markFound() {
		System.out.println("claiming....."+entry.getName());
		this.entry.setItemStatus(ItemStatus.RESOLVED);
		this.entry.setItemType(ItemType.CLAIMED);
		this.entry.setDateClaimed(new Date());
		itemService.updateItem(this.entry);
		System.out.println("claimed.");
		Messages.addFlashGlobalInfo(this.entry.getName() + "has been claimed ");
		try {
			Faces.redirect(VIEW_URL);
		} catch (IOException ex) {
			Messages.addFlashGlobalInfo(this.entry.getName() + "claim failed ");
			ex.printStackTrace();
		} 

	}

	public void lost() {
		entry.setItemType(ItemType.LOST);
		entry.setItemStatus(ItemStatus.PENDING);
		if (file2 == null) {
			itemService.createItem(entry);
			Messages.addFlashGlobalInfo("lost item reported sccessfully");
			try {
				Faces.redirect(VIEW_URL);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		Media m = new Media();
		m.setIdentifier(fileName);
		m.setExtension(extractFileExtension(fileName));
		m.setMediaType(getMediaType(m.getExtension()));
		itemService.createItem(entry, m);
		Messages.addFlashGlobalInfo("lost item reported sccessfully");
		try {
			Faces.redirect(VIEW_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public void found() {
		System.out.println(entry.getDateReported());
		if (file == null) {
			Messages.addFlashGlobalError("upload a file");

			return;
		}
		Media m = new Media();
		m.setIdentifier(fileName);
		m.setExtension(extractFileExtension(fileName));
		m.setMediaType(getMediaType(m.getExtension()));
		entry.setItemType(ItemType.FOUND);
		entry.setItemStatus(ItemStatus.PENDING);
		itemService.createItem(entry, m);
		Messages.addFlashGlobalInfo("item reported sccessfully");
		try {
			Faces.redirect(VIEW_URL);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void clear() {
		this.entry = new Item();
		this.entryMedia = new Media();
	}

	public MediaType getMediaType(String extension) {
		Set<String> imageExtensions = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif"));

		Set<String> videoExtensions = new HashSet<>(Arrays.asList("mp4", "mov", "wmv", "avi", "mkv", "webm"));

		if (imageExtensions.contains(extension)) {
			return MediaType.IMAGE;
		} else if (videoExtensions.contains(extension)) {
			return MediaType.VIDEO;
		} else {
			return null;
		}

	}

	public void handleFileUpload(FileUploadEvent event) {
		this.file = event.getFile();
		if (file != null && file.getSize() > 0) {
			try {
				fileName = getFileIdentifier() + file.getFileName();
				System.out.println(fileName);
				fileName = fileName.replaceAll("[\\s#%&?+:;=@$^(){}\\[\\]<>,'\"]", "_");
				InputStream input = file.getInputstream();
				String uploadDirectory = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/uploads/");
				this.previewPath = Paths.get(uploadDirectory, fileName);
				System.out.println(previewPath);
				Files.copy(input, previewPath, StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				// TODO
			}
		} else {
			// TODO
		}

	}

	public void handleFileUpload2(FileUploadEvent event) {
		this.file2 = event.getFile();
		if (file2 != null && file2.getSize() > 0) {
			try {
				fileName = getFileIdentifier() + file2.getFileName();
				System.out.println(fileName);
				fileName = fileName.replaceAll("[\\s#%&?+:;=@$^(){}\\[\\]<>,'\"]", "_");
				InputStream input = file2.getInputstream();
				String uploadDirectory = FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/uploads/");
				this.previewPath = Paths.get(uploadDirectory, fileName);
				System.out.println(previewPath);
				Files.copy(input, previewPath, StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				// TODO
			}
		} else {
			// TODO
		}

	}

	private String extractFileExtension(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return null;
		}
		int lastDotIndex = fileName.lastIndexOf('.');
		if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
			String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
			return extension;
		}
		return null; // No extension found
	}

	public void loadEntry(Item e) {
		System.out.println(e.getName());
		this.entry = e;
		this.entryMedia = mediaService.findMediaForContent(e.getId());
	}

	public void createNewItemView() throws IOException {
		LOG.info("createNewItemView invoked");
		this.entry = new Item();
	}

	public String getDisplayImage(Category c) {
		switch (c) {
		case GADGET:
			return "lostNfound_gadget.jpg";
		case ID_CARD:
			return "lostNfound_id_card.jpg";
		case BOOK:
			return "lostNfound_books.jpg";
		case CLOTHING:
			return "lostNfound_clothing.jpg";
		case ELECTRONICS:
			return "lostNfound_electronics.jpg";
		case ATM_CARD:
			return "lostNfound_atm_card.jpg";
		case KEYS:
			return "lostNfound_keys.jpg";
		case MATERIALS:
			return "lostNfound_materials.jpg";
		default:
			return "lostNfound.png";

		}

	}

	public void listItem() {
		LOG.info("listItem invoked!!!");
		try {
			// SecurityUtils.getSubject().checkRole("MDOPS");
			Messages.addFlashGlobalInfo("loaded items ");

			setLazyModel(new ItemLazyDataModel(itemService, QueryType.GET_ALL_ITEM));
			setLazyModel1(new ItemLazyDataModel(itemService, QueryType.GET_FOUND));
			setLazyModel2(new ItemLazyDataModel(itemService, QueryType.GET_LOST));
		} catch (Exception e) {
			Messages.addGlobalError("oops error encountered while fetching entries!");
			LOG.error("oops error encountered while fetching entries!", e.fillInStackTrace());
			e.printStackTrace(); //
		}
	}

	public String getFileIdentifier() {
		Date date = new Date();
		String fileName = String.format("lostNfound_media_" + +date.getTime());
		System.out.println(fileName);
		return fileName;
	}

	public LazyDataModel<Item> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModel<Item> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public Item getEntry() {
		return entry;
	}

	public void setEntry(Item entry) {
		this.entry = entry;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param fileExtension the fileExtension to set
	 */
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * @return the previewPath
	 */
	public Path getPreviewPath() {
		return previewPath;
	}

	/**
	 * @param previewPath the previewPath to set
	 */
	public void setPreviewPath(Path previewPath) {
		this.previewPath = previewPath;
	}

	public Media getEntryMedia() {
		return entryMedia;
	}

	public void setEntryMedia(Media entMedia) {
		this.entryMedia = entMedia;
	}

	/**
	 * @return the lazyModel1
	 */
	public LazyDataModel<Item> getLazyModel1() {
		return lazyModel1;
	}

	/**
	 * @param lazyModel1 the lazyModel1 to set
	 */
	public void setLazyModel1(LazyDataModel<Item> lazyModel1) {
		this.lazyModel1 = lazyModel1;
	}

	/**
	 * @return the lazyModel2
	 */
	public LazyDataModel<Item> getLazyModel2() {
		return lazyModel2;
	}

	/**
	 * @param lazyModel2 the lazyModel2 to set
	 */
	public void setLazyModel2(LazyDataModel<Item> lazyModel2) {
		this.lazyModel2 = lazyModel2;
	}

}
