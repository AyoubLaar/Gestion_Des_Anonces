package PFE.Gestion_Des_Anonces.Api.Models.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.Commentaire;
import PFE.Gestion_Des_Anonces.Api.Models.Evaluation.Evaluation;
import PFE.Gestion_Des_Anonces.Api.Models.Privilege.Privilege;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.Reservation;
import PFE.Gestion_Des_Anonces.Api.Models.Role.Role;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails, Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUser;
    @Column(unique = true)
    private String email;
    private String nom, password, prenom;
    private Character sexe;
    private Timestamp dateCreationCompte;
    private LocalDate dateNaissance;
    @OneToMany(mappedBy = "idProprietaire")
    @JsonIgnore
    private List<Anonce> anonces;
    @OneToMany(mappedBy = "idMembre")
    private List<Reservation> reservations;
    @OneToMany(mappedBy = "idMembre")
    private List<Commentaire> commentaires;
    @OneToMany(mappedBy = "idMembre")
    private List<Evaluation> evaluations;
    private STATUS status;
    @ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "idUser"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<Role> roles=new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(List<Role> roles){
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals(STATUS.enabled);
    }

}

