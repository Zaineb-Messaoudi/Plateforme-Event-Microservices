package tn.esprit.microservices.feedback;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrganizerResponseRequest {

    @NotBlank
    @Size(min = 10, max = 2000)
    private String response;

    @NotNull
    private ReclamationStatus status;
}
