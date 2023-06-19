package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import PFE.Gestion_Des_Anonces.Api.utils.TYPE;

import java.util.List;

public record ANONCE_DTO_HUB(
        String Nom ,
        float nbrEtoiles,
        int Surface ,
        int nbreSalleBain,
        int nbreChambres ,
        int nbreEtages ,
        float prix ,
        String imageUrl,
        String description ,
        String  email ,
        String telephone ,
        String ville ,
        String region,
        TYPE type,
        List<COMMENTAIRE_DTO> Commentaires
) {
}
