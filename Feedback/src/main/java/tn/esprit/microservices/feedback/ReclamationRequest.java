package tn.esprit.microservices.feedback;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReclamationRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long eventId;

    @NotNull
    private Long organizerId;

    @NotBlank
    @Size(min = 5, max = 500)
    private String subject;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String description;
}
