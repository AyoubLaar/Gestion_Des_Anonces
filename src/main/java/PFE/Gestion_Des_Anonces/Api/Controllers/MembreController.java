package PFE.Gestion_Des_Anonces.Api.Controllers;

import PFE.Gestion_Des_Anonces.Api.Services.MembreService;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.COMMENTAIRE_DTO_SUBMIT;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.RESERVATION_DTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/Membre")
@RequiredArgsConstructor
public class MembreController {

    @Autowired
    private final MembreService membreService;
    @PostMapping("/Reserver")
    public ResponseEntity<?> reserver(@RequestBody @NonNull RESERVATION_DTO reservation){
        return membreService.reserver(reservation);
    }

    @PostMapping("/Commenter")
    public ResponseEntity<?> commenter(@RequestBody @NonNull COMMENTAIRE_DTO_SUBMIT commentaire){
        return membreService.commenter(commentaire);
    }

    @GetMapping("/Reservations")
    public ResponseEntity<?> getReservations(){
        return membreService.getReservations();
    }

    @GetMapping("/Anonces")
    public ResponseEntity<?> getAnonces(){
        return membreService.getAnonces();
    }

    @PostMapping("/Reservations/cancel")
    public ResponseEntity<?> cancelReservation(@RequestParam @NonNull Long id){
        return membreService.cancelReservation(id);
    }

    @PostMapping("/Reservations/uncancel")
    public ResponseEntity<?> uncancelReservation(@RequestParam @NonNull Long id){
        return membreService.uncancelReservation(id);
    }

    @GetMapping("/UserData")
    public ResponseEntity<?> getUserData(){
        return membreService.getUserData();
    }
}
