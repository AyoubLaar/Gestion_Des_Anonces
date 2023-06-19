package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import java.sql.Timestamp;

public record EVALUATION_DTO_PROPRIETAIRE(
        int nbretoiles,
        String contenu,
        Timestamp datePublication ,
        USER_COMMENT_DTO membre
) {
}
