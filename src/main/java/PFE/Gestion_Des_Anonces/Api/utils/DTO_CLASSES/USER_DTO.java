package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;


import java.time.LocalDate;


public record USER_DTO (
     String prenom,
     String nom,
     String email,
     Character sexe,
     LocalDate dateNaissance
){

}
