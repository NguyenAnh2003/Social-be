package com.example.socialapi.user.requests;

import lombok.Data;

@Data
public class UpdateRequest {
    private String userId;
    private String name;
    private String gender;
}
