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
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.ItemType;
import com.kayode.lostNfound.model.User;
import com.kayode.lostNfound.service.ClaimService;
import com.kayode.lostNfound.service.ItemService;
import com.kayode.lostNfound.service.UserService;

@Named("adminBean")
@ViewScoped
public class AdminBean implements Serializable {

    private List<ClaimDetail> pendingClaimDetails  = new ArrayList<>();
    private List<ClaimDetail> resolvedClaimDetails = new ArrayList<>();
    private Claim selectedClaim;
    private String approvalNotes   = "";
    private String rejectionReason = "";
    private long lostCount;
    private long foundCount;
    private long pendingClaimsCount;

    @Inject private ClaimService claimService;
    @Inject private ItemService  itemService;
    @Inject private UserService  userService;
    @Inject private AuthBean     authBean;

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        pendingClaimDetails  = buildDetails(claimService.findPendingClaims());
        resolvedClaimDetails = buildDetails(claimService.findResolvedClaims());
        lostCount          = itemService.fetchItemCount(ItemType.LOST).longValue();
        foundCount         = itemService.fetchItemCount(ItemType.FOUND).longValue();
        pendingClaimsCount = claimService.countPendingClaims();
    }

    private List<ClaimDetail> buildDetails(List<Claim> claims) {
        List<ClaimDetail> result = new ArrayList<>();
        for (Claim c : claims) {
            Item item  = itemService.findItem(c.getItemId());
            User owner = userService.findById(c.getOwnerId());
            result.add(new ClaimDetail(
                c,
                item  != null ? item.getName()                    : "Unknown Item",
                item  != null ? String.valueOf(item.getCategory()) : "—",
                owner != null ? owner.getName()                   : "Unknown",
                owner != null ? owner.getEmail()                  : "—"
            ));
        }
        return result;
    }

    public void selectClaim(Claim claim) {
        this.selectedClaim    = claim;
        this.approvalNotes    = "";
        this.rejectionReason  = "";
    }

    public void approveClaim() {
        if (selectedClaim == null) return;
        claimService.approveClaim(selectedClaim.getId(),
                authBean.getLoggedInUser().getId(), approvalNotes);
        Messages.addFlashGlobalInfo("Claim approved. Item marked as claimed.");
        loadData();
    }

    public void rejectClaim() {
        if (selectedClaim == null) return;
        claimService.rejectClaim(selectedClaim.getId(),
                authBean.getLoggedInUser().getId(), rejectionReason);
        Messages.addFlashGlobalInfo("Claim rejected.");
        loadData();
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public List<ClaimDetail> getPendingClaimDetails()  { return pendingClaimDetails; }
    public List<ClaimDetail> getResolvedClaimDetails() { return resolvedClaimDetails; }
    public Claim getSelectedClaim()                    { return selectedClaim; }
    public void setSelectedClaim(Claim c)              { this.selectedClaim = c; }
    public String getApprovalNotes()                   { return approvalNotes; }
    public void setApprovalNotes(String v)             { this.approvalNotes = v; }
    public String getRejectionReason()                 { return rejectionReason; }
    public void setRejectionReason(String v)           { this.rejectionReason = v; }
    public long getLostCount()                         { return lostCount; }
    public long getFoundCount()                        { return foundCount; }
    public long getPendingClaimsCount()                { return pendingClaimsCount; }

    // ── Inner DTO ─────────────────────────────────────────────────────────────

    public static class ClaimDetail implements Serializable {
        private final Claim  claim;
        private final String itemName;
        private final String itemCategory;
        private final String ownerName;
        private final String ownerEmail;

        public ClaimDetail(Claim claim, String itemName, String itemCategory,
                           String ownerName, String ownerEmail) {
            this.claim        = claim;
            this.itemName     = itemName;
            this.itemCategory = itemCategory;
            this.ownerName    = ownerName;
            this.ownerEmail   = ownerEmail;
        }

        public Claim  getClaim()        { return claim; }
        public String getItemName()     { return itemName; }
        public String getItemCategory() { return itemCategory; }
        public String getOwnerName()    { return ownerName; }
        public String getOwnerEmail()   { return ownerEmail; }
    }
}
