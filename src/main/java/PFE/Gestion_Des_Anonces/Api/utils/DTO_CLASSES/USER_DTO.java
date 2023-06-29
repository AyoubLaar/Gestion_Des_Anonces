package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;


import PFE.Gestion_Des_Anonces.Api.utils.SEXE;

import java.time.LocalDate;


public record USER_DTO (
     String prenom,
     String nom,
     String email,
     SEXE sexe,
     LocalDate dateNaissance
){

}
