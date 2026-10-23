
package com.example.demo.controller;

import com.example.demo.dto.ClaimRequest;
import com.example.demo.entity.Claim;
import com.example.demo.entity.ClaimStatus;
import com.example.demo.service.ClaimService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // CREATE: Submit a claim
    @PostMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Claim createClaim(
            @PathVariable Long itemId,
            @Valid @RequestBody ClaimRequest request) {
        return claimService.createClaim(itemId, request);
    }

    // READ: All claims
    @GetMapping
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    // READ: Claims for one item
    @GetMapping("/item/{itemId}")
    public List<Claim> getClaimsByItem(@PathVariable Long itemId) {
        return claimService.getClaimsByItem(itemId);
    }

    // APPROVE
    @PatchMapping("/{claimId}/approve")
    public Claim approveClaim(@PathVariable Long claimId) {
        return claimService.updateClaimStatus(
                claimId, ClaimStatus.APPROVED);
    }

    // REJECT
    @PatchMapping("/{claimId}/reject")
    public Claim rejectClaim(@PathVariable Long claimId) {
        return claimService.updateClaimStatus(
                claimId, ClaimStatus.REJECTED);
    }

    // COMPLETE: Item has been returned
    @PatchMapping("/{claimId}/complete")
    public Claim completeClaim(@PathVariable Long claimId) {
        return claimService.completeClaim(claimId);
    }
}
