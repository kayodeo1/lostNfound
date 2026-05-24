package com.kayode.lostNfound.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Messages;

import com.kayode.lostNfound.model.Claim;
import com.kayode.lostNfound.model.ClaimStatus;
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.service.ClaimService;
import com.kayode.lostNfound.service.ItemService;

@Named("claimBean")
@ViewScoped
public class ClaimBean implements Serializable {

    private String proof = "";
    private Item selectedItem = new Item();
    private List<MyClaimDetail> myClaimDetails = new ArrayList<>();

    @Inject private ClaimService claimService;
    @Inject private ItemService  itemService;
    @Inject private AuthBean     authBean;

    @PostConstruct
    public void init() {
        loadMyClaimDetails();
    }

    public void loadItem(Item item) {
        this.selectedItem = item;
        this.proof        = "";
    }

    public void claim(Item item) {
        this.selectedItem = item;
        submitClaim();
    }

    public void submitClaim() {
        if (authBean.getLoggedInUser() == null) {
            Messages.addGlobalError("You must be logged in to claim an item.");
            return;
        }
        if (selectedItem == null || selectedItem.getId() == null) {
            Messages.addGlobalError("No item selected.");
            return;
        }
        if (claimService.hasActiveClaim(selectedItem.getId(), authBean.getLoggedInUser().getId())) {
            Messages.addGlobalError("You already have a pending claim for this item.");
            return;
        }
        Claim c = new Claim();
        c.setItemId(selectedItem.getId());
        c.setOwnerId(authBean.getLoggedInUser().getId());
        c.setProof(proof);
        c.setStatus(ClaimStatus.PENDING);
        claimService.createClaim(c);
        Messages.addFlashGlobalInfo("Claim submitted! An admin will review it shortly.");
        proof        = "";
        selectedItem = new Item();
        loadMyClaimDetails();
    }

    public void loadMyClaimDetails() {
        myClaimDetails = new ArrayList<>();
        if (authBean.getLoggedInUser() == null) return;
        List<Claim> claims = claimService.findClaimsByOwner(authBean.getLoggedInUser().getId());
        for (Claim claim : claims) {
            Item item = itemService.findItem(claim.getItemId());
            myClaimDetails.add(new MyClaimDetail(
                claim,
                item != null ? item.getName()                    : "Unknown Item",
                item != null ? String.valueOf(item.getCategory()) : "—",
                item != null ? item.getItemType()                : null
            ));
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────

    public String getProof()                        { return proof; }
    public void setProof(String p)                  { this.proof = p; }
    public Item getSelectedItem()                   { return selectedItem; }
    public void setSelectedItem(Item i)             { this.selectedItem = i; }
    public List<MyClaimDetail> getMyClaimDetails()  { return myClaimDetails; }

    // ── Inner DTO ─────────────────────────────────────────────────────────

    public static class MyClaimDetail implements Serializable {
        private final Claim  claim;
        private final String itemName;
        private final String itemCategory;
        private final Object itemType;

        public MyClaimDetail(Claim claim, String itemName, String itemCategory, Object itemType) {
            this.claim        = claim;
            this.itemName     = itemName;
            this.itemCategory = itemCategory;
            this.itemType     = itemType;
        }

        public Claim  getClaim()        { return claim; }
        public String getItemName()     { return itemName; }
        public String getItemCategory() { return itemCategory; }
        public Object getItemType()     { return itemType; }
    }
}
