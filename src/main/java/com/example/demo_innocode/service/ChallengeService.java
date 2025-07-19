package com.example.demo_innocode.service;

import com.example.demo_innocode.entity.Challenge;

import java.util.List;

public interface ChallengeService {
    List<Challenge> getAllChallengesByLocationId(Long locationId);
    List<Challenge> getAllChallengesByLocationName(String locationName);
    List<Challenge> getAllChallenges();
}
