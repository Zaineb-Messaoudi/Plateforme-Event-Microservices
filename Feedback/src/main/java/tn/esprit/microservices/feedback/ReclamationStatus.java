package tn.esprit.microservices.feedback;

public enum ReclamationStatus {
    PENDING,      // submitted, awaiting organizer response
    IN_PROGRESS,  // organizer acknowledged
    RESOLVED,     // organizer responded / resolved
    REJECTED,     // organizer rejected the reclamation
    CLOSED        // admin closed
}
