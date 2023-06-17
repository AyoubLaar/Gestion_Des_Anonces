package PFE.Gestion_Des_Anonces.Api.Services;

import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.Commentaire;
import PFE.Gestion_Des_Anonces.Api.Models.Commentaire.CommentaireRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.Reservation;
import PFE.Gestion_Des_Anonces.Api.Models.Reservation.ReservationRepository;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Models.User.UserRepository;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.*;
import PFE.Gestion_Des_Anonces.Api.utils.STATUS;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembreService {
    @Autowired
    private final AnonceRepository anonceRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ReservationRepository reservationRepository;
    @Autowired
    private final CommentaireRepository commentaireRepository;

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
            if(reservation.DateReservationDepart().isBefore(reservation.DateReservationArrive())
            || reservation.DateReservationDepart().isBefore(LocalDate.now())
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
            Optional<Reservation> res = reservationRepository.findById(id);
            if(res.isEmpty())throw new Exception();
            Reservation reservation = res.get();
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
            Reservation reservation = res.get();
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
                user.getPassword(),
                user.getSexe(),
                user.getDateNaissance()
        ));
        /*
     String firstName,
     String lastName,
     String email,
     String password ,
     Character sexe,
     LocalDate dateNaissance
     * */
    }
}
