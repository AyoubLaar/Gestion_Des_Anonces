package PFE.Gestion_Des_Anonces.Api.Config.Startup;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.Categorie;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.CategorieRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.Commentaire;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.CommentaireRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Privilege.Privilege;
import PFE.Gestion_Des_Anonces.Api.Models.Privilege.PrivilegeRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Region.Region;
import PFE.Gestion_Des_Anonces.Api.Models.Region.RegionRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnonceRepository anonceRepository;
    @Autowired
    private RegionRepository regionRepository;

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
        createRoleIfNotFound("ADMIN", adminPrivileges);
        createRoleIfNotFound("MEMBRE", Collections.singletonList(readPrivilege));

        List<Role> adminRole = roleRepository.findByName("ADMIN");
        List<Role> membreRole = roleRepository.findByName("MEMBRE");
        if(userRepository.findByEmail("test@test.com").isEmpty()){
        User user = new User();
        user.setNom("Test");
        user.setPrenom("Test");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("test@test.com");
        user.setRoles(adminRole);
        user.setStatus(STATUS.enabled);
        userRepository.save(user);
        }
        if(userRepository.findByEmail("Test_Membre@test.com").isEmpty()) {
            User user1 = new User();
            user1.setNom("Test_Membre");
            user1.setPrenom("Test_Membre");
            user1.setPassword(passwordEncoder.encode("test"));
            user1.setEmail("Test_Membre@test.com");
            user1.setRoles(membreRole);
            user1.setStatus(STATUS.enabled);
            userRepository.save(user1);
        }
        saveAnonces();
        saveAnonces();
        saveAnonces();
        saveAnonces();
        saveAnonces();
        alreadySetup = true;
    }

    private void saveAnonces(){
        User user1 = userRepository.findAll().get(1);
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
        Region casaStat = Region
                .builder()
                .idRegion("Casablanca-settat")
                .build();
        regionRepository.save(casaStat);
        Ville casa = Ville
                .builder()
                .idVille("Casablanca")
                .idRegion(casaStat)
                .build();
        casaStat = regionRepository.findAll().get(0);
        casa.setIdRegion(casaStat);
        villeRepository.save(casa);
        Categories = categorieRepository.findAll();
        comments = commentaireRepository.findAll();
        casa = villeRepository.findAll().get(0);

        List<Anonce> anonces = List.of(
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.enabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&w=350&dpr=2")
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
                        .build(),
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.enabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&w=350&dpr=2")
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
                        .build(),
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.enabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://i.insider.com/60ae84bea412370019d321ff?width=700")
                        .latitude((float)Math.random()*90)
                        .longitude((float)Math.random()*180)
                        .nomAnonce("Serena no Piscine")
                        .type(TYPE.location)
                        .prix(100)
                        .telephone("0694853606")
                        .surface(400)
                        .nbreChambres(5)
                        .nbreEtages(1)
                        .nbreSalleBain(10)
                        .build(),
                Anonce.builder()
                        .email("email@email.com")
                        .status(STATUS.disabled)
                        .dateCreationAnonce(Timestamp.valueOf(LocalDateTime.now()))
                        .description("Nestled amidst breathtaking  decor, plush furnishings.")
                        .idProprietaire(user1)
                        .imageUrl("https://i.insider.com/60ae84bea412370019d321ff?width=700")
                        .latitude((float)Math.random()*90)
                        .longitude((float)Math.random()*180)
                        .nomAnonce("Disabled")
                        .type(TYPE.achat)
                        .prix(100)
                        .telephone("0694853606")
                        .surface(400)
                        .nbreChambres(5)
                        .nbreEtages(1)
                        .nbreSalleBain(10)
                        .build());
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
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, List<Privilege> privileges) {
        List<Role> roleList = roleRepository.findByName(name);
        if (roleList.size() == 0) {
            Role role = new Role();
            role.setName(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
            roleList.add(role);
        }
        return roleList.get(0);
    }
}