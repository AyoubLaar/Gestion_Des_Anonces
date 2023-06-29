package PFE.Gestion_Des_Anonces.Api.Models.Role;

import PFE.Gestion_Des_Anonces.Api.Models.Privilege.Privilege;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Role {

    @Id
    private String id;
    @ManyToMany(mappedBy = "roles" ,fetch = FetchType.EAGER)
    private List<User> users;

    @ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "id"))
    private List<Privilege> privileges;

}