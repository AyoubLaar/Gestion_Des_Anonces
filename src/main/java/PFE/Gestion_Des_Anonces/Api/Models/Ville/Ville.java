package PFE.Gestion_Des_Anonces.Api.Models.Ville;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Region.Pays;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="Ville")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ville implements Serializable {
    @Id
    private String idVille;

    @ManyToOne()
    @JoinColumn(name = "idPays")
    private Pays idPays;

    @OneToMany(mappedBy = "idVille")
    private List<Anonce> Anonces;

}

