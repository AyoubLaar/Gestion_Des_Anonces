package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import PFE.Gestion_Des_Anonces.Api.utils.TYPE;

import java.util.List;

public record ANONCE_DTO_MODIFY(
        STATUS status,
        String Nom ,
        float nbrEtoiles,
        int Surface ,
        int nbreSalleBain,
        int nbreChambres ,
        String adresse,
        int nbreEtages ,
        float prix ,
        String imageUrl,
        String description ,
        String  email ,
        String telephone ,
        String ville ,
        String pays,
        TYPE type,
        List<RESERVATION_DTO_PROPRIETAIRE> reservations ,
        List<EVALUATION_DTO_PROPRIETAIRE> evaluations
) {
}
