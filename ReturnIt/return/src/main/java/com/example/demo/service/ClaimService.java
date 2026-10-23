
package com.example.demo.service;

import com.example.demo.dto.ClaimRequest;
import com.example.demo.entity.Claim;
import com.example.demo.entity.ClaimStatus;
import com.example.demo.entity.Item;
import com.example.demo.repository.ClaimRepository;
import com.example.demo.repository.ItemRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;

    public ClaimService(
            ClaimRepository claimRepository,
            ItemRepository itemRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
    }

    // CREATE: Submit a claim
    public Claim createClaim(Long itemId, ClaimRequest request) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found with ID: " + itemId
                ));

        boolean alreadyClaimed =
                claimRepository
                    .existsByItem_IdAndClaimantNameIgnoreCaseAndStatus(
                        itemId,
                        request.getClaimantName().trim(),
                        ClaimStatus.PENDING
                    );

        if (alreadyClaimed) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have a pending claim for this item"
            );
        }

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimantName(request.getClaimantName().trim());
        claim.setMessage(request.getMessage());
        claim.setStatus(ClaimStatus.PENDING);

        return claimRepository.save(claim);
    }

    // READ: All claims
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // READ: Claims for one item
    public List<Claim> getClaimsByItem(Long itemId) {

        if (!itemRepository.existsById(itemId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found with ID: " + itemId
            );
        }

        return claimRepository.findByItem_Id(itemId);
    }

    // APPROVE or REJECT a pending claim
    @Transactional
    public Claim updateClaimStatus(
            Long claimId,
            ClaimStatus newStatus) {

        if (newStatus != ClaimStatus.APPROVED
                && newStatus != ClaimStatus.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use APPROVED or REJECTED for this operation"
            );
        }

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Claim not found with ID: " + claimId
                ));

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending claims can be approved or rejected"
            );
        }

        // If approving, reject other pending claims for this item
        if (newStatus == ClaimStatus.APPROVED) {

            List<Claim> pendingClaims =
                    claimRepository.findByItem_IdAndStatus(
                            claim.getItem().getId(),
                            ClaimStatus.PENDING
                    );

            for (Claim otherClaim : pendingClaims) {
                if (!otherClaim.getId().equals(claimId)) {
                    otherClaim.setStatus(ClaimStatus.REJECTED);
                    claimRepository.save(otherClaim);
                }
            }
        }

        claim.setStatus(newStatus);
        return claimRepository.save(claim);
    }

    // COMPLETE: Confirm the item was returned
    @Transactional
    public Claim completeClaim(Long claimId) {

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Claim not found with ID: " + claimId
                ));

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only approved claims can be completed"
            );
        }

        Item item = claim.getItem();
        item.setStatus("RETURNED");

        itemRepository.save(item);

        claim.setStatus(ClaimStatus.COMPLETED);
        return claimRepository.save(claim);
    }
}
