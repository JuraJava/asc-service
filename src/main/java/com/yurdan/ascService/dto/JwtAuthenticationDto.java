package com.yurdan.ascService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record JwtAuthenticationDto(@JsonProperty("sub") UUID userId,
                                   @JsonProperty("roles") List<String> roles,
                                   @JsonProperty("iat") Date createDate,
                                   @JsonProperty("exp") Date expirationDate) {}
