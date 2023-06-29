package PFE.Gestion_Des_Anonces.Api.Services;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.Categorie;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.CategorieRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Evaluation.Evaluation;
import PFE.Gestion_Des_Anonces.Api.Models.Region.Region;
import PFE.Gestion_Des_Anonces.Api.Models.Region.RegionRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.Reservation;
import PFE.Gestion_Des_Anonces.Api.Models.Role.Role;
import PFE.Gestion_Des_Anonces.Api.Models.Role.RoleRepository;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Models.User.UserRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.Ville;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.VilleRepository;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.ANONCE_DTO_SEARCH;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.USER_ADMIN_DTO;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.USER_DTO;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private AnonceRepository anonceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private RegionRepository regionRepository;

    public ResponseEntity<?> getAnonces() {
        List<Anonce> anonces = anonceRepository.findAll();
        List<Map<String,String>> anoncesDtos = new ArrayList<>();
        for(Anonce anonce : anonces){
            Map<String , String> anonceDto = new HashMap<>();
            anonceDto.put("idAnonce",anonce.getIdAnonce()+"");
            anonceDto.put("nomAnonce",anonce.getNomAnonce());
            anonceDto.put("type",anonce.getType().name());
            anonceDto.put("idVille",anonce.getIdVille().getIdVille());
            anonceDto.put("prix",anonce.getPrix()+"");
            anonceDto.put("dateCreation",anonce.getDateCreationAnonce().toString());
            anonceDto.put("status",anonce.getStatus().name());
            anoncesDtos.add(anonceDto);
        }
        return ResponseEntity.ok(anoncesDtos);
    }

    @Autowired
    private RoleRepository roleRepository;

    public ResponseEntity<?> getUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users.stream().filter(user -> {
            Optional<Role> roleOptional = roleRepository.findById("ADMIN");
            if(roleOptional.isEmpty())return true;
            Role adminRole = roleOptional.get();
            if(user.getRoles().contains(adminRole))return false;
            return true;
        }).map(user -> new USER_ADMIN_DTO(
                user.getIdUser(),
                user.getPrenom(),
                user.getNom(),
                user.getEmail(),
                user.getDateCreationCompte(),
                user.getStatus()
                )).toList());
    }

    public ResponseEntity<?> addCategorie(String categorieString) {
        Optional<Categorie> categorieOpt = categorieRepository.findById(categorieString);
        if(!categorieOpt.isEmpty())return ResponseEntity.badRequest().build();
        Categorie categorie = Categorie.builder().idCategorie(categorieString).build();
        categorieRepository.save(categorie);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> addVille(String villeString, String regionString) {
        Optional<Ville> villeOptional = villeRepository.findById(villeString);
        if(villeOptional.isEmpty()){
            Optional<Region> regionOptional  = regionRepository.findById(regionString);
            Region region=null;
            if(regionOptional.isEmpty()){
                region = Region.builder().idRegion(regionString).build();
                regionRepository.save(region);
            }else{
                region = regionOptional.get();
            }
            Ville ville = Ville.builder().idVille(villeString).idRegion(region).build();
            villeRepository.save(ville);
            return ResponseEntity.ok().build();
        }else{
            Ville ville = villeOptional.get();
            if(ville.getIdRegion().getIdRegion().equals(regionString))
                return ResponseEntity.badRequest().build();
            else{
                Optional<Region> regionOptional  = regionRepository.findById(regionString);
                if(regionOptional.isEmpty()){
                    Region region = Region.builder().idRegion(regionString).build();
                    regionRepository.save(region);
                    ville.setIdRegion(region);
                    villeRepository.save(ville);
                }else{
                    Region region = regionOptional.get();
                    ville.setIdRegion(region);
                    villeRepository.save(ville);
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.ok().build();
            }
        }
    }
    public ResponseEntity<?> toggleAnonce(Long id) {
        Optional<Anonce> anonceOptional = anonceRepository.findById(id);
        if(anonceOptional.isEmpty())return ResponseEntity.badRequest().build();
        Anonce anonce = anonceOptional.get();
        if(anonce.getStatus().equals(STATUS.removed))return ResponseEntity.badRequest().build();
        if(anonce.getStatus().equals(STATUS.enabled)) {
            anonce.setStatus(STATUS.adminDisabled);
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }
        if(anonce.getStatus().equals(STATUS.userDisabled)){
            anonce.setStatus(STATUS.userAdminDisabled);
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }
        if(anonce.getStatus().equals(STATUS.adminDisabled)){
            anonce.setStatus(STATUS.enabled);
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }
        if(anonce.getStatus().equals(STATUS.userAdminDisabled)){
            anonce.setStatus(STATUS.userDisabled);
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<?> toggleUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty())return ResponseEntity.badRequest().build();
        User user = userOptional.get();
        if(user.getStatus().equals(STATUS.removed))return ResponseEntity.badRequest().build();
        if(user.getStatus().equals(STATUS.enabled)){
            user.setStatus(STATUS.adminDisabled);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        if(user.getStatus().equals(STATUS.adminDisabled)){
            user.setStatus(STATUS.enabled);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<?> getAnonce(Long id) {
        Optional<Anonce> anonceOptional = anonceRepository.findById(id);
        if(anonceOptional.isEmpty())return ResponseEntity.badRequest().build();
        Anonce anonce = anonceOptional.get();
        List<Reservation> reservations = anonce.getReservations();
        List<Map<String,Object>> reservationsDto = new ArrayList<>();
        for(Reservation reservation : reservations){
            Map<String , Object> reservationDto = new HashMap<>();
            reservationDto.put("dateArrive",reservation.getDateReservationArrive().toString());
            reservationDto.put("dateDepart",reservation.getDateReservationDepart().toString());
            reservationDto.put("dateCreation",reservation.getDateReservation().toString());
            reservationDto.put("email",reservation.getEmailClient());
            reservationDto.put("telephone",reservation.getTelephoneClient());
            reservationDto.put("enfants",reservation.getNbrEnfants());
            reservationDto.put("adultes",reservation.getNbrAdultes());
            reservationDto.put("status",reservation.getStatus());
            reservationDto.put("idUser",reservation.getIdMembre().getIdUser());
            reservationDto.put("idReservation",reservation.getIdReservation());
            Evaluation evaluation = reservation.getEvaluation();
            reservationDto.put("etoiles",evaluation.getNbretoiles());
            reservationsDto.add(reservationDto);
        }
        Map<String , Object> anonceDto = new HashMap<>();
        anonceDto.put("reservations",reservationsDto);
        return ResponseEntity.ok(anonceDto);
    }
}
