package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.entity.Challenge;
import com.example.demo_innocode.repository.ChallengeRepository;
import com.example.demo_innocode.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Override
    public List<Challenge> getAllChallengesByLocationId(Long locationId) {
        return challengeRepository.findByLocationIdWithLocation(locationId);
    }

    @Override
    public List<Challenge> getAllChallengesByLocationName(String locationName) {
        return challengeRepository.findByLocationName(locationName);
    }

    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }
}