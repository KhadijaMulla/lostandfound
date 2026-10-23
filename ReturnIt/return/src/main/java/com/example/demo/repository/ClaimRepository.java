
package com.example.demo.repository;

import com.example.demo.entity.Claim;
import com.example.demo.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByItem_Id(Long itemId);

    boolean existsByItem_IdAndClaimantNameIgnoreCaseAndStatus(
            Long itemId,
            String claimantName,
            ClaimStatus status
    );

    List<Claim> findByItem_IdAndStatus(
            Long itemId,
            ClaimStatus status
    );
}
