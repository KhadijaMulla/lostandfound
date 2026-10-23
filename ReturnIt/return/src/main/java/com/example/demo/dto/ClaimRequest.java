
package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClaimRequest {

    @NotBlank(message = "Claimant name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String claimantName;

    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    public ClaimRequest() {
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
