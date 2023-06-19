package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;


import PFE.Gestion_Des_Anonces.Api.utils.TYPE;

public record ANONCE_DTO_SEARCH(
        long idAnonce,
        float nbreEtoiles,
        float prix, float latitude , float longitude ,
        TYPE type , Boolean etat,
        String imageUrl,
        String nomAnonce,
        String idVille ,
        String idRegion
){
}
