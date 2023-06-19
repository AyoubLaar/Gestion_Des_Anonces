package PFE.Gestion_Des_Anonces.Api.Models.Commentaire;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Table(name = "Commentaire")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Commentaire  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCommentaire;

    private String contenu;

    private Timestamp DatePublication;

    @ManyToOne
    @JoinColumn(name = "idMembre")
    private User idMembre;

    @ManyToOne
    @JoinColumn(name = "idAnonce")
    private Anonce idAnonce;
}

