package PFE.Gestion_Des_Anonces.Api.Models.Reservation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Reservation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idReservation;
    @ManyToOne
    @JoinColumn(name = "idAnonce")
    private Anonce idAnonce;

    @ManyToOne
    @JoinColumn(name = "idMembre")
    private User idMembre;

    private LocalDate DateReservationArrive;
    private LocalDate DateReservationDepart;
    private int nbrEnfants;
    private int nbrAdultes;
    private String emailClient;
    private String telephoneClient;
    private STATUS status;

}
