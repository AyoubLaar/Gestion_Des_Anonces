package PFE.Gestion_Des_Anonces.Api.Services;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.Categorie;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.CategorieRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.Commentaire;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.CommentaireRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.Reservation;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.ReservationRepository;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Models.User.UserRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.Ville;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.VilleRepository;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.*;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembreService {

    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AnonceRepository anonceRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ReservationRepository reservationRepository;
    @Autowired
    private final CommentaireRepository commentaireRepository;
    @Autowired
    private final VilleRepository villeRepository;
    @Autowired
    private final CategorieRepository categorieRepository;


    @Transactional
    public ResponseEntity<?> reserver(RESERVATION_DTO reservation) {
        // add date interval verification with accepted reservations 9:50 -> 80%
        Optional<Anonce> anonceOptional = anonceRepository.findById(reservation.id());
        if(anonceOptional.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        int count = reservationRepository.countReservations(reservation.id(),reservation.DateReservationArrive(),reservation.DateReservationDepart());
        if(count > 0 ){
            return ResponseEntity.badRequest().build();
        }
        Anonce anonce = anonceOptional.get();
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User)principal;
            LocalDate now = LocalDate.now();
            if(reservation.DateReservationDepart().isBefore(reservation.DateReservationArrive())
                    || reservation.DateReservationDepart().isBefore(now)
            ){
                throw new Exception();
            }
            Reservation res = Reservation
                    .builder()
                    .DateReservationArrive(reservation.DateReservationArrive())
                    .DateReservationDepart(reservation.DateReservationDepart())
                    .status(STATUS.pending)
                    .nbrAdultes(reservation.nbrAdultes())
                    .nbrEnfants(reservation.nbrEnfants())
                    .emailClient(reservation.emailClient())
                    .telephoneClient(reservation.telephoneClient())
                    .build();
            res.setIdAnonce(anonce);
            res.setIdMembre(user);
            reservationRepository.save(res);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> commenter(COMMENTAIRE_DTO_SUBMIT commentaire) {
        Optional<Anonce> anonceOptional = anonceRepository.findById(commentaire.idAnonce());
        if(anonceOptional.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Anonce anonce = anonceOptional.get();
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User)principal;
            Commentaire res = Commentaire
                    .builder()
                    .idMembre(user)
                    .contenu(commentaire.text())
                    .idAnonce(anonce)
                    .DatePublication(new Timestamp(System.currentTimeMillis()))
                    .build();
            commentaireRepository.save(res);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getReservations() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User) principal;
            user = userRepository.findById(user.getIdUser()).get();
            List<Reservation> reservations = user.getReservations();
            return ResponseEntity.ok().body(reservations.stream().map(reservation -> new RESERVATION_PROFIL_DTO(
                    reservation.getIdReservation(),
                    reservation.getIdAnonce().getIdAnonce(),
                    reservation.getIdAnonce().getNomAnonce(),
                    reservation.getDateReservationArrive(),
                    reservation.getDateReservationDepart(),
                    reservation.getEmailClient(),
                    reservation.getTelephoneClient(),
                    reservation.getNbrEnfants(),
                    reservation.getNbrAdultes(),
                    reservation.getStatus()
            )).filter(res -> res.status() != STATUS.cancelled).toList());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> cancelReservation(Long id){
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User) principal;
            user = userRepository.findById(user.getIdUser()).get();
            Optional<Reservation> res = reservationRepository.findById(id);
            if(res.isEmpty())throw new Exception();
            Reservation reservation = res.get();
            if(reservation.getIdMembre().getIdUser() != user.getIdUser())throw new Exception();
            reservation.setStatus(STATUS.cancelled);
            reservationRepository.save(reservation);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getAnonces() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user = userRepository.findById(user.getIdUser()).get();
        List<Anonce> anonces = user.getAnonces();
        if(anonces.isEmpty())return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(anonces.stream().map(anonce -> new ANONCE_DTO_SEARCH(
                anonce.getIdAnonce(),
                0,
                anonce.getPrix(),
                anonce.getLatitude(),
                anonce.getLongitude(),
                anonce.getType(),
                anonce.getEnabled(),
                anonce.getImageUrl(),
                anonce.getNomAnonce(),
                anonce.getIdVille().getIdVille(),
                anonce.getIdVille().getIdRegion().getIdRegion()
        )).toList());
    }

    public ResponseEntity<?> uncancelReservation(Long id) {
        try{
            Optional<Reservation> res = reservationRepository.findById(id);
            if(res.isEmpty())throw new Exception();
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User) principal;
            user = userRepository.findById(user.getIdUser()).get();
            Reservation reservation = res.get();
            if(reservation.getIdMembre().getIdUser() != user.getIdUser())throw new Exception();
            reservation.setStatus(STATUS.pending);
            reservationRepository.save(reservation);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getUserData() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user = userRepository.findById(user.getIdUser()).get();
        return ResponseEntity.ok(new USER_DTO(
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getSexe(),
                user.getDateNaissance()
        ));
    }

    public ResponseEntity<?> modifyUserData(User request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user = userRepository.findById(user.getIdUser()).get();
        try {
            if(! (user.getStatus() == STATUS.enabled))throw new Exception();
            if (request.getNom() != null)
                user.setNom(request.getNom());
            if (request.getPrenom() != null)
                user.setPrenom(request.getPrenom());
            if (request.getPassword() != null)
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            if (request.getDateNaissance() != null)
                user.setDateNaissance(request.getDateNaissance());
            if (request.getSexe() != null)
                user.setSexe(request.getSexe());
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }catch (Exception E){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getAnonceDetails(long id) {
        Optional<Anonce> anonoceOpt = anonceRepository.findById(id);
        if(anonoceOpt.isEmpty())return ResponseEntity.badRequest().build();
        else{
            try{
                Anonce A = anonoceOpt.get();
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User user = (User) principal;
                user = userRepository.findById(user.getIdUser()).get();
                if(A.getIdProprietaire().getIdUser() != user.getIdUser())throw new Exception();
                List<RESERVATION_DTO_PROPRIETAIRE> reservations = A.getReservations().stream().map(
                        reservation -> new RESERVATION_DTO_PROPRIETAIRE(
                                reservation.getIdReservation(),
                                reservation.getIdMembre().getNom(),
                                reservation.getIdMembre().getPrenom(),
                                reservation.getDateReservationArrive(),
                                reservation.getDateReservationDepart(),
                                reservation.getEmailClient(),
                                reservation.getTelephoneClient(),
                                reservation.getNbrEnfants(),
                                reservation.getNbrAdultes(),
                                reservation.getStatus()
                                )
                ).toList();
                List<EVALUATION_DTO_PROPRIETAIRE> evaluations = A.getEvaluations().stream().map(evaluation ->
                        new EVALUATION_DTO_PROPRIETAIRE(
                                evaluation.getNbretoiles(),
                                evaluation.getContenu(),
                                evaluation.getDatePublication(),
                                new USER_COMMENT_DTO(
                                        evaluation.getIdMembre().getNom(),
                                        evaluation.getIdMembre().getPrenom()
                                )
                        )).toList();
                return ResponseEntity.ok(new ANONCE_DTO_MODIFY(
                        A.getNomAnonce(),
                        anonceRepository.getStars(A.getIdAnonce()),
                        A.getSurface(),
                        A.getNbreSalleBain(),
                        A.getNbreChambres(),
                        A.getNbreEtages(),
                        A.getPrix(),
                        A.getImageUrl(),
                        A.getDescription(),
                        A.getEmail(),
                        A.getTelephone(),
                        A.getIdVille().getIdVille(),
                        A.getIdVille().getIdRegion().getIdRegion(),
                        A.getType(),
                        reservations,
                        evaluations
                ));

            }catch(Exception e){
                return ResponseEntity.badRequest().build();
            }
        }
    }

    public ResponseEntity<?> publier(ANONCE_DTO_PUBLIER DTO){
            if (    DTO == null ||
                    DTO.type() == null ||
                    DTO.prix() < 0.0f ||
                    DTO.surface() < 0 ||
                    DTO.chambres() < 0 ||
                    DTO.sallesDeBain() < 0 ||
                    DTO.etages() < 0 ||
                    DTO.nomAnonce().isEmpty() ||
                    DTO.description().isEmpty() ||
                    DTO.imageUrl().isEmpty() ||
                    DTO.email().isEmpty() ||
                    DTO.telephone().isEmpty() ||
                    DTO.ville().isEmpty() ||
                    DTO.region().isEmpty() ||
                    DTO.categories() == null || DTO.categories().length == 0) {
                return ResponseEntity.badRequest().build();
            }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user = userRepository.findById(user.getIdUser()).get();
        if(user.isEnabled()) {
            Optional<Ville> villeOptional = villeRepository.findById(DTO.ville());
            if(villeOptional.isEmpty() || !villeOptional.get().getIdRegion().getIdRegion().equals(DTO.region()))
                return ResponseEntity.badRequest().build();
            Ville ville = villeOptional.get();
            List<Categorie> categories = new ArrayList<>();
            for(int i = 0 ; i < DTO.categories().length ; i++){
                Optional<Categorie> categorie = categorieRepository.findById(DTO.categories()[i]);
                if(categorie.isEmpty()){
                    return ResponseEntity.badRequest().build();
                }
                categories.add(categorie.get());
            }
            Anonce anonce = Anonce.builder()
                    .prix(DTO.prix())
                    .idVille(ville)
                    .categories(categories)
                    .surface(DTO.surface())
                    .nomAnonce(DTO.nomAnonce())
                    .nbreEtages(DTO.etages())
                    .nbreChambres(DTO.chambres())
                    .email(DTO.email())
                    .nbreSalleBain(DTO.sallesDeBain())
                    .longitude(DTO.longitude())
                    .latitude(DTO.latitude())
                    .imageUrl(DTO.imageUrl())
                    .idProprietaire(user)
                    .description(DTO.description())
                    .type(DTO.type())
                    .dateCreationAnonce(new Timestamp(System.currentTimeMillis()))
                    .status(STATUS.enabled)
                    .telephone(DTO.telephone())
                    .build();
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getAnonceData(Long id) {
        Optional<Anonce> anonceOptional = anonceRepository.findById(id);
        if(anonceOptional.isEmpty())return ResponseEntity.badRequest().build();
        else{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = (User) principal;
            user = userRepository.findById(user.getIdUser()).get();
            Anonce anonce = anonceOptional.get();
            if(anonce.getIdProprietaire().getIdUser() != user.getIdUser())return ResponseEntity.badRequest().build();
            List<Categorie> categoriesList = anonce.getCategories();
            String [] categories = new String[categoriesList.size()];
            for(int i = 0 ; i < categories.length ; i++){
                categories[i] = categoriesList.get(i).getIdCategorie();
            }
            return ResponseEntity.ok(new ANONCE_DTO_PUBLIER(
                    anonce.getType(),
                    anonce.getLatitude(),
                    anonce.getLongitude(),
                    anonce.getPrix(),
                    anonce.getSurface(),
                    anonce.getNbreChambres(),
                    anonce.getNbreSalleBain(),
                    anonce.getNbreEtages(),
                    anonce.getNomAnonce(),
                    anonce.getDescription(),
                    anonce.getImageUrl(),
                    anonce.getEmail(),
                    anonce.getTelephone(),
                    anonce.getIdVille().getIdVille(),
                    anonce.getIdVille().getIdRegion().getIdRegion(),
                    categories
            ));
        }
    }

    public ResponseEntity<?> modifierAnonce(Long id , ANONCE_DTO_PUBLIER DTO){
        if (    DTO == null ||
                DTO.type() == null ||
                DTO.prix() < 0.0f ||
                DTO.surface() < 0 ||
                DTO.chambres() < 0 ||
                DTO.sallesDeBain() < 0 ||
                DTO.etages() < 0 ||
                DTO.nomAnonce().isEmpty() ||
                DTO.description().isEmpty() ||
                DTO.imageUrl().isEmpty() ||
                DTO.email().isEmpty() ||
                DTO.telephone().isEmpty() ||
                DTO.ville().isEmpty() ||
                DTO.region().isEmpty() ||
                DTO.categories() == null || DTO.categories().length == 0) {
            return ResponseEntity.badRequest().build();
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user = userRepository.findById(user.getIdUser()).get();
        if(user.isEnabled()) {
            Optional<Ville> villeOptional = villeRepository.findById(DTO.ville());
            if(villeOptional.isEmpty() || !villeOptional.get().getIdRegion().getIdRegion().equals(DTO.region()))
                return ResponseEntity.badRequest().build();
            Ville ville = villeOptional.get();
            List<Categorie> categories = new ArrayList<>();
            for(int i = 0 ; i < DTO.categories().length ; i++){
                Optional<Categorie> categorie = categorieRepository.findById(DTO.categories()[i]);
                if(categorie.isEmpty()){
                    return ResponseEntity.badRequest().build();
                }
                categories.add(categorie.get());
            }
            Optional<Anonce> anonceOptional = anonceRepository.findById(id);
            if(anonceOptional.isEmpty() || anonceOptional.get().getIdProprietaire().getIdUser() != user.getIdUser()){
                return ResponseEntity.badRequest().build();
            }
            Anonce anonce = anonceOptional.get();
            anonce.setPrix(DTO.prix());
            anonce.setIdVille(ville);
            anonce.setCategories(categories);
            anonce.setSurface(DTO.surface());
            anonce.setNomAnonce(DTO.nomAnonce());
            anonce.setNbreEtages(DTO.etages());
            anonce.setNbreChambres(DTO.chambres());
            anonce.setEmail(DTO.email());
            anonce.setNbreSalleBain(DTO.sallesDeBain());
            anonce.setLongitude(DTO.longitude());
            anonce.setLatitude(DTO.latitude());
            anonce.setImageUrl(DTO.imageUrl());
            anonce.setIdProprietaire(user);
            anonce.setDescription(DTO.description());
            anonce.setType(DTO.type());
            anonce.setStatus(STATUS.enabled);
            anonce.setTelephone(DTO.telephone());
            anonceRepository.save(anonce);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }
    }


    public ResponseEntity<?> getAnonceReservations(Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user = userRepository.findById(user.getIdUser()).get();
        Optional<Anonce> anonceOptional = anonceRepository.findById(id);
        if(anonceOptional.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Anonce anonce = anonceOptional.get();
        if(anonce.getIdProprietaire().getIdUser() != user.getIdUser() || !anonce.getEnabled())return ResponseEntity.badRequest().build();
        List<Reservation> reservations = anonce.getReservations();
        return ResponseEntity.ok(reservations.stream().map(reservation -> new RESERVATION_DTO_ANONCE(
                reservation.getIdReservation(),
                reservation.getDateReservationArrive(),
                reservation.getDateReservationDepart(),
                reservation.getEmailClient(),
                reservation.getTelephoneClient(),
                reservation.getNbrEnfants(),
                reservation.getNbrAdultes(),
                reservation.getStatus())
            ).toList()
        );
    }
}