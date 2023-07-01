package PFE.Gestion_Des_Anonces.Api.Models.Region;

import PFE.Gestion_Des_Anonces.Api.Models.Ville.Ville;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pays implements Serializable {

        @Column(name="idPays")
        @Id
        private String idPays;

        @OneToMany(mappedBy="idPays")
        private List<Ville> villes;
}
