package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import java.time.LocalDate;

public record RESERVATION_DTO(
        Long id ,
        LocalDate DateReservationArrive,
        LocalDate DateReservationDepart,
        String emailClient,
        String telephoneClient,
        Integer nbrEnfants,
        Integer nbrAdultes


) {
}
