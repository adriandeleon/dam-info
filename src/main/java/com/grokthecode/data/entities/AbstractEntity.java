package com.grokthecode.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgenerator")
    @SequenceGenerator(name = "idgenerator")
    private Long id;

    @Version
    private int version;

    @Column(name = "soft_delete")
    private Boolean softDelete;

    @CreatedDate
    @Column( name = "created_date", nullable = false, updatable = false)
    private Timestamp createdDate;

/*
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
*/

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private Timestamp lastModifiedDate;

/*    @LastModifiedBy
   @Column(name = "last_modified_by", nullable = false)
   private String lastModifiedBy;*/

    @PrePersist
    public void setAuditInfo() {
        this.setSoftDelete(false);
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity that)) {
            return false; // null or not an AbstractEntity class
        }
        if (getId() != null) {
            return getId().equals(that.getId());
        }
        return super.equals(that);
    }
}
