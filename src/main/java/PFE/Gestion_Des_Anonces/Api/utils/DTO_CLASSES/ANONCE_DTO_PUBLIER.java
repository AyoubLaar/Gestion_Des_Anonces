package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import PFE.Gestion_Des_Anonces.Api.utils.TYPE;

public record ANONCE_DTO_PUBLIER(
        TYPE type,
        float latitude,
        float longitude,
        float prix,
        int surface,
        int chambres,
        int sallesDeBain,
        int etages,
        String nomAnonce,
        String description,
        String imageUrl,
        String email,
        String telephone,
        String ville,
        String region,
        String [] categories
) {
}
