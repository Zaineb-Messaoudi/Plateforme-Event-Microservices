package tn.esprit.microservices.abonnement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.response.PlanResponse;
import tn.esprit.microservices.abonnement.enums.PlanType;
import tn.esprit.microservices.abonnement.service.SubscriptionPlanService;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final SubscriptionPlanService planService;


    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllActivePlans());
    }


    @GetMapping("/{planType}")
    public ResponseEntity<PlanResponse> getPlanByType(@PathVariable PlanType planType) {
        return ResponseEntity.ok(planService.getPlanByType(planType));
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }
}