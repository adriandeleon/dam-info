package com.grokthecode.data.repositories;

import com.grokthecode.data.entities.DamCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DamCatalogRepository  extends JpaRepository<DamCatalogEntity, Long>, JpaSpecificationExecutor<DamCatalogEntity> {

    Optional<DamCatalogEntity> findBySihKey(String sihKey);
}
