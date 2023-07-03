package PFE.Gestion_Des_Anonces.Api.Services;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.Categorie;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.CategorieRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Evaluation.Evaluation;
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
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.USER_ADMIN_DTO;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private PaysRepository paysRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ReservationRepository reservationRepository;

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
            anonceDto.put("idProprietaire", String.valueOf(anonce.getIdProprietaire().getIdUser()));
            anoncesDtos.add(anonceDto);
        }
        return ResponseEntity.ok(anoncesDtos);
    }


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
            Optional<Pays> paysOptional  = paysRepository.findById(regionString);
            Pays pays=null;
            if(paysOptional.isEmpty()){
                pays = Pays.builder().idPays(regionString).build();
                paysRepository.save(pays);
            }else{
                pays = paysOptional.get();
            }
            Ville ville = Ville.builder().idVille(villeString).idPays(pays).build();
            villeRepository.save(ville);
            return ResponseEntity.ok().build();
        }else{
            Ville ville = villeOptional.get();
            if(ville.getIdPays().getIdPays().equals(regionString))
                return ResponseEntity.badRequest().build();
            else{
                Optional<Pays> regionOptional  = paysRepository.findById(regionString);
                if(regionOptional.isEmpty()){
                    Pays pays = Pays.builder().idPays(regionString).build();
                    paysRepository.save(pays);
                    ville.setIdPays(pays);
                    villeRepository.save(ville);
                }else{
                    Pays pays = regionOptional.get();
                    ville.setIdPays(pays);
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
        if(anonce.getStatus().equals(STATUS.removed) || !anonce.getIdProprietaire().isEnabled())return ResponseEntity.badRequest().build();
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
            List<Anonce> anonces = user.getAnonces();
            List<Reservation> reservations = user.getReservations();
            for(Anonce anonce:anonces){
                if(anonce.getStatus().equals(STATUS.enabled)) {
                    anonce.setStatus(STATUS.disabledWithUser);
                    List<Reservation> reservations1 = anonce.getReservations();
                    for (Reservation reservation : reservations1) {
                        if (reservation.getStatus().equals(STATUS.pending))
                            reservation.setStatus(STATUS.cancelled);
                    }
                    reservationRepository.saveAll(reservations1);
                }
            }
            anonceRepository.saveAll(anonces);
            for(Reservation reservation:reservations){
                if(reservation.getStatus().equals(STATUS.pending))
                    reservation.setStatus(STATUS.cancelled);
            }
            user.setStatus(STATUS.adminDisabled);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        if(user.getStatus().equals(STATUS.adminDisabled)){
            List<Anonce> anonces = user.getAnonces();
            for(Anonce anonce:anonces){
                if(anonce.getStatus().equals(STATUS.disabledWithUser)) {
                    anonce.setStatus(STATUS.enabled);
                }
            }
            anonceRepository.saveAll(anonces);
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
            if(evaluation != null) {
                reservationDto.put("etoiles", evaluation.getNbretoiles());
            }else{
                reservationDto.put("etoiles",null);
            }
            reservationsDto.add(reservationDto);
        }
        Map<String , Object> anonceDto = new HashMap<>();
        anonceDto.put("reservations",reservationsDto);
        anonceDto.put("idAnonce",anonce.getIdAnonce());
        anonceDto.put("idProprietaire",anonce.getIdProprietaire().getIdUser());
        anonceDto.put("nom",anonce.getNomAnonce());
        anonceDto.put("imageUrl",anonce.getImageUrl());
        anonceDto.put("surface",anonce.getSurface());
        anonceDto.put("etages",anonce.getNbreEtages());
        anonceDto.put("chambres",anonce.getNbreChambres());
        anonceDto.put("salles",anonce.getNbreSalleBain());
        anonceDto.put("prix",String.valueOf(anonce.getPrix()));
        anonceDto.put("description",anonce.getDescription());
        anonceDto.put("email",anonce.getEmail());
        anonceDto.put("telephone",anonce.getTelephone());
        anonceDto.put("ville",anonce.getIdVille().getIdVille());
        anonceDto.put("pays",anonce.getIdVille().getIdPays().getIdPays());
        anonceDto.put("type",anonce.getType());
        anonceDto.put("status",anonce.getStatus());
        anonceDto.put("dateCreation",anonce.getDateCreationAnonce().toString());
        anonceDto.put("adresse",anonce.getAdresse());
        float nbretoiles = anonceRepository.getStars(anonce.getIdAnonce());
        anonceDto.put("nbretoiles",nbretoiles);
        return ResponseEntity.ok(anonceDto);
    }

    public ResponseEntity<?> supprimerAnonce(Long id) {
        Optional<Anonce> anonceOptional = anonceRepository.findById(id);
        if(anonceOptional.isEmpty())return ResponseEntity.badRequest().build();
        Anonce anonce = anonceOptional.get();
        if(anonce.getStatus().equals(STATUS.adminRemoved) || anonce.getStatus().equals(STATUS.removed)){
            return ResponseEntity.ok().build();
        }else{
            List<Reservation> reservations1 = anonce.getReservations();
            for(Reservation reservation:reservations1){
                if(reservation.getStatus().equals(STATUS.pending))
                    reservation.setStatus(STATUS.cancelled);
            }
            reservationRepository.saveAll(reservations1);
            anonce.setStatus(STATUS.adminRemoved);
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<?> getUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty())return ResponseEntity.badRequest().build();
        User user = userOptional.get();
        List<Reservation> reservations = user.getReservations();
        List<Anonce> anonces = user.getAnonces();
        List<Map<String,Object>> reservationsDto = new ArrayList<>();
        List<Map<String,Object>> anoncesDto = new ArrayList<>();
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
            reservationDto.put("idAnonce",reservation.getIdAnonce().getIdAnonce());
            reservationDto.put("idReservation",reservation.getIdReservation());
            Evaluation evaluation = reservation.getEvaluation();
            if(evaluation != null) {
                reservationDto.put("etoiles", evaluation.getNbretoiles());
            }else{
                reservationDto.put("etoiles",null);
            }
            reservationsDto.add(reservationDto);
        }

        for(Anonce anonce : anonces){
            Map<String,Object> anonceDto = new HashMap<>();
            anonceDto.put("idAnonce",anonce.getIdAnonce());
            anonceDto.put("nom",anonce.getNomAnonce());
            anonceDto.put("prix",anonce.getPrix());
            anonceDto.put("ville",anonce.getIdVille().getIdVille());
            anonceDto.put("type",anonce.getType());
            anonceDto.put("status",anonce.getStatus());
            anonceDto.put("dateCreation",anonce.getDateCreationAnonce().toString());
            anoncesDto.add(anonceDto);
        }

        Map<String , Object> userDto = new HashMap<>();
        userDto.put("reservations",reservationsDto);
        userDto.put("anonces",anoncesDto);
        userDto.put("idUser",user.getIdUser());
        userDto.put("nom",user.getNom());
        userDto.put("prenom",user.getPrenom());
        userDto.put("sexe",user.getSexe());
        userDto.put("dateNaissance",user.getDateNaissance());
        userDto.put("email",user.getEmail());
        userDto.put("status",user.getStatus());
        userDto.put("dateCreation",user.getDateCreationCompte());
        return ResponseEntity.ok(userDto);
    }

    public ResponseEntity<?> supprimerUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty())return ResponseEntity.badRequest().build();
        User user = userOptional.get();
        List<Anonce> anonces = user.getAnonces();
        List<Reservation> reservations = user.getReservations();
        for(Anonce anonce:anonces){
            anonce.setStatus(STATUS.adminRemoved);
            List<Reservation> reservations1 = anonce.getReservations();
            for(Reservation reservation:reservations1){
                if(reservation.getStatus().equals(STATUS.pending))
                    reservation.setStatus(STATUS.cancelled);
            }
            reservationRepository.saveAll(reservations1);
        }
        anonceRepository.saveAll(anonces);
        for(Reservation reservation:reservations){
            if(reservation.getStatus().equals(STATUS.pending))
                reservation.setStatus(STATUS.cancelled);
        }
        reservationRepository.saveAll(reservations);
        user.setStatus(STATUS.adminRemoved);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
