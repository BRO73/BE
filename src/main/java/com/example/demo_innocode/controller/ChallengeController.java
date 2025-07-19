// Controller
package com.example.demo_innocode.controller;

import com.example.demo_innocode.entity.Challenge;
import com.example.demo_innocode.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    // GET /api/challenges?locationId=1
    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String locationName) {

        List<Challenge> challenges;

        if (locationId != null) {
            challenges = challengeService.getAllChallengesByLocationId(locationId);
        } else if (locationName != null) {
            challenges = challengeService.getAllChallengesByLocationName(locationName);
        } else {
            challenges = challengeService.getAllChallenges();
        }

        return ResponseEntity.ok(challenges);
    }

    // GET /api/challenges/location/{locationId}
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Challenge>> getChallengesByLocationId(@PathVariable Long locationId) {
        List<Challenge> challenges = challengeService.getAllChallengesByLocationId(locationId);
        return ResponseEntity.ok(challenges);
    }

    // GET /api/challenges/location/name/{locationName}
    @GetMapping("/location/name/{locationName}")
    public ResponseEntity<List<Challenge>> getChallengesByLocationName(@PathVariable String locationName) {
        List<Challenge> challenges = challengeService.getAllChallengesByLocationName(locationName);
        return ResponseEntity.ok(challenges);
    }
}