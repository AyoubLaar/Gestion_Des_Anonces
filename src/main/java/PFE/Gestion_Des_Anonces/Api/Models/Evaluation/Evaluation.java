package PFE.Gestion_Des_Anonces.Api.Models.Evaluation;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.Reservation;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Evaluation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Evaluation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEvaluation;

    private Integer nbretoiles;

    private String contenu;

    private Timestamp datePublication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMembre")
    private User idMembre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAnonce")
    private Anonce idAnonce;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation")
    private Reservation reservation;
}

