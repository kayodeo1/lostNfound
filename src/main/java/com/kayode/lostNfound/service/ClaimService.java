package com.kayode.lostNfound.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.kayode.lostNfound.model.Claim;
import com.kayode.lostNfound.model.ClaimStatus;
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.ItemStatus;
import com.kayode.lostNfound.model.ItemType;
import com.kayode.lostNfound.model.ReleaseRecord;
import com.kayode.lostNfound.model.StatusHistory;

@Stateless
public class ClaimService {

    @PersistenceContext(unitName = "app")
    private EntityManager em;

    @Inject
    private ItemService itemService;

    public void createClaim(Claim c) {
        em.persist(c);
        em.flush();
    }

    public List<Claim> findPendingClaims() {
        TypedQuery<Claim> q = em.createQuery(
            "SELECT c FROM Claim c WHERE c.status = :status ORDER BY c.createdAt DESC", Claim.class);
        q.setParameter("status", ClaimStatus.PENDING);
        return q.getResultList();
    }

    public List<Claim> findClaimsByOwner(Long ownerId) {
        TypedQuery<Claim> q = em.createQuery(
            "SELECT c FROM Claim c WHERE c.ownerId = :ownerId ORDER BY c.createdAt DESC", Claim.class);
        q.setParameter("ownerId", ownerId);
        return q.getResultList();
    }

    public List<Claim> findResolvedClaims() {
        TypedQuery<Claim> q = em.createQuery(
            "SELECT c FROM Claim c WHERE c.status != :status ORDER BY c.createdAt DESC", Claim.class);
        q.setParameter("status", ClaimStatus.PENDING);
        return q.getResultList();
    }

    public long countPendingClaims() {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(c.id) FROM Claim c WHERE c.status = :status", Long.class);
        q.setParameter("status", ClaimStatus.PENDING);
        return q.getSingleResult();
    }

    public boolean hasActiveClaim(Long itemId, Long ownerId) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(c.id) FROM Claim c WHERE c.itemId = :itemId AND c.ownerId = :ownerId AND c.status = :status",
            Long.class);
        q.setParameter("itemId", itemId);
        q.setParameter("ownerId", ownerId);
        q.setParameter("status", ClaimStatus.PENDING);
        return q.getSingleResult() > 0;
    }

    public void approveClaim(Long claimId, Long adminId, String notes) {
        Claim claim = em.find(Claim.class, claimId);
        if (claim == null) return;

        StatusHistory history = new StatusHistory();
        history.setClaimId(claimId);
        history.setOldStatus(claim.getStatus().name());
        history.setNewStatus(ClaimStatus.APPROVED.name());
        history.setChangedBy(adminId);
        em.persist(history);

        claim.setStatus(ClaimStatus.APPROVED);
        em.merge(claim);

        Item item = itemService.findItem(claim.getItemId());
        if (item != null) {
            item.setItemType(ItemType.CLAIMED);
            item.setItemStatus(ItemStatus.RESOLVED);
            item.setDateClaimed(new Date());
            itemService.updateItem(item);
        }

        ReleaseRecord record = new ReleaseRecord();
        record.setClaimId(claimId);
        record.setAdminId(adminId);
        record.setNotes(notes);
        em.persist(record);
        em.flush();
    }

    public void rejectClaim(Long claimId, Long adminId, String reason) {
        Claim claim = em.find(Claim.class, claimId);
        if (claim == null) return;

        StatusHistory history = new StatusHistory();
        history.setClaimId(claimId);
        history.setOldStatus(claim.getStatus().name());
        history.setNewStatus(ClaimStatus.REJECTED.name());
        history.setChangedBy(adminId);
        em.persist(history);

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(reason);
        em.merge(claim);
        em.flush();
    }
}
