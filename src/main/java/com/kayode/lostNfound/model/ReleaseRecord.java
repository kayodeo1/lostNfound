package com.kayode.lostNfound.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class ReleaseRecord extends AbstractEntity {

    private Long claimId;
    private Long adminId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseDate;

    @Column(length = 1000)
    private String notes;

    @PrePersist
    private void onCreate() {
        if (releaseDate == null) releaseDate = new Date();
    }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
