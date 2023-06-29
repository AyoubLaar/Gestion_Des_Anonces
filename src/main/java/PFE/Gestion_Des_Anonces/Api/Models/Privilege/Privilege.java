package PFE.Gestion_Des_Anonces.Api.Models.Privilege;

import PFE.Gestion_Des_Anonces.Api.Models.Role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Privilege {

    @Id
    private String id;

    @ManyToMany(mappedBy = "privileges" ,fetch = FetchType.LAZY)
    private List<Role> roles;
}