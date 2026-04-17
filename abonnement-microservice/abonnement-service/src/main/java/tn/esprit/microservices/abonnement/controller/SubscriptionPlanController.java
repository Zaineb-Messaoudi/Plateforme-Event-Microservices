package tn.esprit.microservices.abonnement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.entity.SubscriptionPlan;
import tn.esprit.microservices.abonnement.enums.PlanType;
import tn.esprit.microservices.abonnement.service.SubscriptionPlanService;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RefreshScope
public class SubscriptionPlanController {

    @Autowired
    private SubscriptionPlanService planService;

    @Value("${welcome.message}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }

    @GetMapping
    public List<SubscriptionPlan> getAllPlans() {
        return planService.getAllPlans();
    }

    @GetMapping("/active")
    public List<SubscriptionPlan> getActivePlans() {
        return planService.getActivePlans();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getPlanById(@PathVariable Long id) {
        return planService.getPlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{planType}")
    public ResponseEntity<SubscriptionPlan> getPlanByType(@PathVariable PlanType planType) {
        return planService.getPlanByType(planType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlan plan) {
        return ResponseEntity.ok(planService.savePlan(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updatePlan(@PathVariable Long id,
                                                        @RequestBody SubscriptionPlan plan) {
        return ResponseEntity.ok(planService.updatePlan(id, plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
