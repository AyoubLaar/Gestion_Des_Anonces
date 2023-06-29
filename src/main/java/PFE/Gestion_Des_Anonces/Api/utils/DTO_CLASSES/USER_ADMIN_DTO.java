package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import PFE.Gestion_Des_Anonces.Api.utils.STATUS;

import java.sql.Timestamp;

public record USER_ADMIN_DTO(
        Long id,
        String prenom,
        String nom,
        String email,
        Timestamp dateCreation,
        STATUS status
) {
}
