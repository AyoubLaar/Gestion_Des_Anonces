package PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES;

import PFE.Gestion_Des_Anonces.Api.utils.STATUS;

import java.time.LocalDate;

public record RESERVATION_DTO_ANONCE (
      Long id,
      LocalDate DateReservationArrive,
      LocalDate DateReservationDepart,
      String emailClient,
      String telephoneClient,
      Integer nbrEnfants,
      Integer nbrAdultes,
      STATUS status
){
}
