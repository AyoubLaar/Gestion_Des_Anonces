package PFE.Gestion_Des_Anonces.Api.Config.Startup;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.Categorie;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.CategorieRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.Commentaire;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.CommentaireRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Privilege.Privilege;
import PFE.Gestion_Des_Anonces.Api.Models.Privilege.PrivilegeRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Region.Pays;
import PFE.Gestion_Des_Anonces.Api.Models.Region.PaysRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.Reservation;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.ReservationRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Role.Role;
import PFE.Gestion_Des_Anonces.Api.Models.Role.RoleRepository;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Models.User.UserRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.Ville;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.VilleRepository;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import PFE.Gestion_Des_Anonces.Api.utils.TYPE;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnonceRepository anonceRepository;
    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        Role adminRole=createRoleIfNotFound("ADMIN", adminPrivileges);
        Role membreRole=createRoleIfNotFound("MEMBRE", Collections.singletonList(readPrivilege));

        if(userRepository.findByEmail("admin@admin.com").isEmpty()){
        List<Role> roles = new ArrayList<>();
        roles.add(adminRole);
        User user = new User();
        user.setNom("Test");
        user.setPrenom("Test");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@admin.com");
        user.setRoles(roles);
        user.setStatus(STATUS.enabled);
        user.setDateCreationCompte(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        }
        if(userRepository.findByEmail("membre@membre.com").isEmpty()) {
            List<Role> roles = new ArrayList<>();
            roles.add(membreRole);
            User user1 = new User();
            user1.setNom("Test_Membre");
            user1.setPrenom("Test_Membre");
            user1.setPassword(passwordEncoder.encode("membre"));
            user1.setEmail("membre@membre.com");
            user1.setRoles(roles);
            user1.setStatus(STATUS.enabled);
            user1.setDateCreationCompte(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user1);
        }
        saveAnonces();
        alreadySetup = true;
    }

    private void saveAnonces(){
        List<User> users = userRepository.findAll();
        User user1 = users.get(1);
        User user2 = users.get(0);

        List<Commentaire> comments = List.of(
                Commentaire.builder()
                        .contenu("Hadchi nadi nadi bezaf!")
                        .idMembre(user1)
                        .DatePublication(Timestamp.valueOf(LocalDateTime.now()))
                        .build(),
                Commentaire.builder()
                        .contenu("Hadchi nadi nadi bezaf!")
                        .idMembre(user1)
                        .DatePublication(Timestamp.valueOf(LocalDateTime.now()))
                        .build(),
                Commentaire.builder()
                        .contenu("Hadchi nadi nadi bezaf!")
                        .idMembre(user1)
                        .DatePublication(Timestamp.valueOf(LocalDateTime.now()))
                        .build(),
                Commentaire.builder()
                        .contenu("Hadchi nadi nadi bezaf!")
                        .idMembre(user1)
                        .DatePublication(Timestamp.valueOf(LocalDateTime.now()))
                        .build()
        );
        for(Commentaire C : comments){
            commentaireRepository.save(C);
        }
        List<Categorie> Categories = List.of(
                Categorie
                        .builder()
                        .idCategorie("Appartement")
                        .build(),
                Categorie
                        .builder()
                        .idCategorie("Piscine")
                        .build(),
                Categorie
                        .builder()
                        .idCategorie("Autre")
                        .build()
        );
        for(Categorie C : Categories){
            categorieRepository.save(C);
        }
        Pays maroc = Pays
                .builder()
                .idPays("Maroc")
                .build();
        paysRepository.save(maroc);
        Ville casa = Ville
                .builder()
                .idVille("Casablanca")
                .build();
        maroc = paysRepository.findAll().get(0);
        casa.setIdPays(maroc);
        villeRepository.save(casa);
        Categories = categorieRepository.findAll();
        casa = villeRepository.findAll().get(0);

        List<Anonce> anonces = List.of(
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.enabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://res.cloudinary.com/drkbf7big/image/upload/v1688150466/ln1xsdyxaupdag0bpxni.jpg")
                        .latitude((float)Math.random()*90)
                        .longitude((float)Math.random()*180)
                        .nomAnonce("Serena Hub")
                        .type(TYPE.location)
                        .prix(100)
                        .telephone("0694853606")
                        .surface(400)
                        .nbreChambres(5)
                        .nbreEtages(1)
                        .nbreSalleBain(10)
                        .adresse("Derb Sultan Derb Bouchentouf rue 60")
                        .dateCreationAnonce(new Timestamp(System.currentTimeMillis()))
                        .build(),
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.enabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://res.cloudinary.com/drkbf7big/image/upload/v1688221505/doo0a6vfa1bbcfxia993.jpg")
                        .latitude((float)Math.random()*90)
                        .longitude((float)Math.random()*180)
                        .nomAnonce("Serena no Appartement")
                        .type(TYPE.achat)
                        .prix(100)
                        .telephone("0694853606")
                        .surface(400)
                        .nbreChambres(5)
                        .nbreEtages(1)
                        .nbreSalleBain(10)
                        .adresse("Derb Sultan Derb Bouchentouf rue 40")
                        .dateCreationAnonce(new Timestamp(System.currentTimeMillis()))
                        .build(),
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.adminDisabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://res.cloudinary.com/drkbf7big/image/upload/v1688368410/bfl3acmrnzhv6infqbui.jpg")
                        .latitude((float)Math.random()*90)
                        .longitude((float)Math.random()*180)
                        .nomAnonce("Disabled")
                        .type(TYPE.achat)
                        .prix(100)
                        .telephone("0694853606")
                        .surface(400)
                        .nbreChambres(5)
                        .adresse("Derb Sultan Derb Bouchentouf rue 55")
                        .nbreEtages(1)
                        .nbreSalleBain(10)
                        .dateCreationAnonce(new Timestamp(System.currentTimeMillis()))
                        .build()
        );
        comments = commentaireRepository.findAll();
        int i = 0;
        for(Commentaire C : comments){
            C.setIdAnonce(anonces.get(i%anonces.size()));
            i++;
        }
        for(Anonce X : anonces) {
            X.setCategories(Categories);
            X.setIdVille(casa);
        }
        anonces.get(1).setCategories(List.of(Categories.get(1)));
        anonces.get(2).setCategories(List.of(Categories.get(0)));
        anonceRepository.saveAll(anonces);
        users = userRepository.findAll();
        user2 = users.get(1);
        anonces = anonceRepository.findAll();
        Reservation reservation = Reservation.builder()
                .dateReservation(new Timestamp(System.currentTimeMillis()))
                .telephoneClient("0684629206")
                .emailClient("ayoublaarouchi03@gmail.com")
                .idAnonce(anonces.get(0))
                .nbrEnfants(10)
                .nbrAdultes(10)
                .dateReservationArrive(LocalDate.of(2023,5,1))
                .dateReservationDepart(LocalDate.of(2023,5,20))
                .idMembre(user2)
                .status(STATUS.accepted)
                .build();
        Reservation reservation1 = Reservation.builder()
                .dateReservation(new Timestamp(System.currentTimeMillis()))
                .telephoneClient("0684629206")
                .emailClient("ayoublaarouchi03@gmail.com")
                .idAnonce(anonces.get(0))
                .nbrEnfants(10)
                .nbrAdultes(10)
                .dateReservationArrive(LocalDate.of(2023,5,1))
                .dateReservationDepart(LocalDate.of(2023,5,20))
                .idMembre(user2)
                .status(STATUS.accepted)
                .build();
        reservationRepository.save(reservation);
        reservationRepository.save(reservation1);
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Optional<Privilege> privilegeOptional = privilegeRepository.findById(name);
        if (privilegeOptional.isEmpty()) {
            Privilege privilege = new Privilege(name,null);
            privilegeRepository.save(privilege);
            return  privilegeRepository.findById(privilege.getId()).get();
        }
        return privilegeOptional.get();
    }

    @Transactional
    Role createRoleIfNotFound(
        String name, List<Privilege> privileges) {
        Optional<Role> roleOptional = roleRepository.findById(name);
        if (roleOptional.isEmpty()) {
            Role role = new Role(name,null,null);
            role.setPrivileges(privileges);
            roleRepository.save(role);
            return roleRepository.findById(role.getId()).get();
        }
        return roleOptional.get();
    }
}