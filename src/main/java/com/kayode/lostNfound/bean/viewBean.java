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
import javax.servlet.http.HttpSession;

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
import com.kayode.lostNfound.model.User;
import com.kayode.lostNfound.service.ItemService;
import com.kayode.lostNfound.service.MediaService;

@Named("viewBean")
@ViewScoped
public class viewBean implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(viewBean.class);
    private static final String VIEW_URL = Constants.APP_BASE_NAME + "/view2.xhtml?faces-redirect=true";

    @Inject private ItemService itemService;
    @Inject private MediaService mediaService;

    // Selected item for the detail dialog
    private Item entry = new Item();
    private Media entryMedia;

    // Separate form objects for the report dialog
    private Item foundEntry = new Item();
    private Item lostEntry  = new Item();

    // File uploads (separate per form tab)
    private UploadedFile foundFile;
    private UploadedFile lostFile;
    private String foundFileName;
    private String lostFileName;

    // Lazy models for Found / Lost tabs and admin all-items view
    private LazyDataModel<Item> lazyModelFound;
    private LazyDataModel<Item> lazyModelLost;
    private LazyDataModel<Item> lazyModelAll;

    // My Reports
    private List<Item> myItems;

    // Search
    private String searchTerm;

    // Form data
    private List<Category> categories;

    @PostConstruct
    public void init() {
        categories = Arrays.asList(Category.values());
    }

    // Called by f:viewAction on view2.xhtml and list.xhtml
    public void listItem() {
        lazyModelFound = new ItemLazyDataModel(itemService, QueryType.GET_FOUND, searchTerm);
        lazyModelLost  = new ItemLazyDataModel(itemService, QueryType.GET_LOST,  searchTerm);
        lazyModelAll   = new ItemLazyDataModel(itemService, QueryType.GET_ALL_ITEM);
        loadMyItems();
    }

    public void applySearch() {
        listItem();
    }

    public void clearSearch() {
        searchTerm = null;
        listItem();
    }

    public void loadMyItems() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session == null) return;
        User u = (User) session.getAttribute("loggedInUser");
        if (u == null) return;
        myItems = itemService.fetchItemsByUserId(u.getId());
    }

    public void loadEntry(Item e) {
        this.entry      = e;
        this.entryMedia = mediaService.findMediaForContent(e.getId());
    }

    public void createNewItemView() {
        foundEntry   = new Item();
        lostEntry    = new Item();
        foundFile    = null;
        lostFile     = null;
        foundFileName = null;
        lostFileName  = null;
    }

    public void clear() {
        entry      = new Item();
        entryMedia = null;
    }

    // ── Report a FOUND item ────────────────────────────────────────────────

    public void found() {
        if (foundFile == null) {
            Messages.addGlobalError("Please upload a photo or video of the item.");
            return;
        }
        applyCurrentUser(foundEntry);
        foundEntry.setItemType(ItemType.FOUND);
        foundEntry.setItemStatus(ItemStatus.PENDING);

        Media m = buildMedia(foundFileName);
        itemService.createItem(foundEntry, m);
        LOG.info("Found item reported: {}", foundEntry.getName());
        Messages.addFlashGlobalInfo("Found item reported successfully.");
        foundEntry = new Item();
        foundFile  = null;
        try { Faces.redirect(VIEW_URL); } catch (IOException e) { LOG.error("Redirect failed", e); }
    }

    // ── Report a LOST item ─────────────────────────────────────────────────

    public void lost() {
        applyCurrentUser(lostEntry);
        lostEntry.setItemType(ItemType.LOST);
        lostEntry.setItemStatus(ItemStatus.PENDING);

        if (lostFile == null) {
            itemService.createItem(lostEntry);
        } else {
            Media m = buildMedia(lostFileName);
            itemService.createItem(lostEntry, m);
        }
        LOG.info("Lost item reported: {}", lostEntry.getName());
        Messages.addFlashGlobalInfo("Lost item reported successfully.");
        lostEntry = new Item();
        lostFile  = null;
        try { Faces.redirect(VIEW_URL); } catch (IOException e) { LOG.error("Redirect failed", e); }
    }

    // ── File upload handlers ───────────────────────────────────────────────

    public void handleFoundFileUpload(FileUploadEvent event) {
        this.foundFile     = event.getFile();
        this.foundFileName = saveUpload(event.getFile());
    }

    public void handleLostFileUpload(FileUploadEvent event) {
        this.lostFile     = event.getFile();
        this.lostFileName = saveUpload(event.getFile());
    }

    private String saveUpload(UploadedFile file) {
        if (file == null || file.getSize() == 0) return null;
        try {
            String name = "lostNfound_" + new Date().getTime() + "_" + file.getFileName();
            name = name.replaceAll("[\\s#%&?+:;=@$^(){}\\[\\]<>,'\"]", "_");
            String dir  = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/uploads/");
            Files.copy(file.getInputstream(), Paths.get(dir, name), StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (IOException e) {
            LOG.error("File upload failed", e);
            return null;
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void applyCurrentUser(Item item) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            User u = (User) session.getAttribute("loggedInUser");
            item.setUserId(u.getId());
        }
    }

    private Media buildMedia(String fileName) {
        Media m = new Media();
        m.setIdentifier(fileName);
        String ext = extractExtension(fileName);
        m.setExtension(ext);
        m.setMediaType(resolveMediaType(ext));
        return m;
    }

    private String extractExtension(String fileName) {
        if (fileName == null) return null;
        int dot = fileName.lastIndexOf('.');
        return (dot > 0 && dot < fileName.length() - 1)
            ? fileName.substring(dot + 1).toLowerCase()
            : null;
    }

    private MediaType resolveMediaType(String ext) {
        Set<String> imgs   = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif"));
        Set<String> videos = new HashSet<>(Arrays.asList("mp4", "mov", "wmv", "avi", "mkv", "webm"));
        if (ext == null) return null;
        if (imgs.contains(ext))   return MediaType.IMAGE;
        if (videos.contains(ext)) return MediaType.VIDEO;
        return null;
    }

    public String getDisplayImage(Category c) {
        if (c == null) return "lostNfound.png";
        switch (c) {
            case GADGET:      return "lostNfound_gadget.jpg";
            case ID_CARD:     return "lostNfound_id_card.jpg";
            case BOOK:        return "lostNfound_books.jpg";
            case CLOTHING:    return "lostNfound_clothing.jpg";
            case ELECTRONICS: return "lostNfound_electronics.jpg";
            case ATM_CARD:    return "lostNfound_atm_card.jpg";
            case KEYS:        return "lostNfound_keys.jpg";
            case MATERIALS:   return "lostNfound_materials.jpg";
            default:          return "lostNfound.png";
        }
    }

    // ── Getters / Setters ──────────────────────────────────────────────────

    public Item getEntry()                         { return entry; }
    public void setEntry(Item e)                   { this.entry = e; }
    public Media getEntryMedia()                   { return entryMedia; }
    public void setEntryMedia(Media m)             { this.entryMedia = m; }

    public Item getFoundEntry()                    { return foundEntry; }
    public void setFoundEntry(Item e)              { this.foundEntry = e; }
    public Item getLostEntry()                     { return lostEntry; }
    public void setLostEntry(Item e)               { this.lostEntry = e; }

    public LazyDataModel<Item> getLazyModelFound() { return lazyModelFound; }
    public LazyDataModel<Item> getLazyModelLost()  { return lazyModelLost; }
    public LazyDataModel<Item> getLazyModel()      { return lazyModelAll; }
    public LazyDataModel<Item> getLazyModelAll()   { return lazyModelAll; }

    public List<Item> getMyItems()                 { return myItems; }

    public String getSearchTerm()                  { return searchTerm; }
    public void setSearchTerm(String v)            { this.searchTerm = v; }

    public List<Category> getCategories()          { return categories; }
    public void setCategories(List<Category> c)    { this.categories = c; }

    // Needed for the file upload field binding
    public UploadedFile getFoundFile()             { return foundFile; }
    public void setFoundFile(UploadedFile f)       { this.foundFile = f; }
    public UploadedFile getLostFile()              { return lostFile; }
    public void setLostFile(UploadedFile f)        { this.lostFile = f; }
}
