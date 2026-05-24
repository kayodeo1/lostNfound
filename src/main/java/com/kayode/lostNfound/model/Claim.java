package com.kayode.lostNfound.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "claims")
public class Claim extends AbstractEntity {

    private Long itemId;
    private Long ownerId;

    @Column(length = 2000)
    private String proof;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(length = 1000)
    private String rejectionReason;

    @PrePersist
    private void onCreate() {
        createdAt = new Date();
        if (status == null) status = ClaimStatus.PENDING;
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getProof() { return proof; }
    public void setProof(String proof) { this.proof = proof; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
