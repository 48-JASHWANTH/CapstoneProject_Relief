package org.hartford.relief.repository;

import org.hartford.relief.entity.ClaimDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {
}
