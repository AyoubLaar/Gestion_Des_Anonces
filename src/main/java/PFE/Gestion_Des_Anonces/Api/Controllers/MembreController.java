package PFE.Gestion_Des_Anonces.Api.Controllers;

import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Services.FileService;
import PFE.Gestion_Des_Anonces.Api.Services.MembreService;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.ANONCE_DTO_PUBLIER;
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

    @Autowired
    private final FileService fileService;
    @DeleteMapping("DeleteFile")
    public ResponseEntity<?> test(@RequestParam @NonNull String public_id){
        return fileService.deleteImage(public_id);
    }

    @PostMapping("/Reserver")
    public ResponseEntity<?> reserver(@RequestBody @NonNull RESERVATION_DTO reservation){
        return membreService.reserver(reservation);
    }

    @PostMapping("/Publier")
    public ResponseEntity<?> publier(@RequestBody @NonNull ANONCE_DTO_PUBLIER anonce){
        return membreService.publier(anonce);
    }

    @PostMapping("/Commenter")
    public ResponseEntity<?> commenter(@RequestBody @NonNull COMMENTAIRE_DTO_SUBMIT commentaire){
        return membreService.commenter(commentaire);
    }

    @GetMapping("/Reservations")
    public ResponseEntity<?> getReservations(){
        return membreService.getReservations();
    }
    @GetMapping("/Anonce")
    public ResponseEntity<?> getAnonce(@RequestParam long id){
        return membreService.getAnonceDetails(id);
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

    @GetMapping("/User/Retrieve")
    public ResponseEntity<?> getUserData(){
        return membreService.getUserData();
    }

    @GetMapping("/Anonce/Retrieve")
    public ResponseEntity<?> getAnonceData(@RequestParam @NonNull Long id){
        return membreService.getAnonceData(id);
    }

    @GetMapping("/Anonce/Retrieve/Reservations")
    public ResponseEntity<?> getAnonceReservations(@RequestParam @NonNull Long id){
        return membreService.getAnonceReservations(id);
    }

    @PostMapping("/Modify/User")
    public ResponseEntity<?> modifyUserData(@RequestBody @NonNull User request){
        return membreService.modifyUserData(request);
    }

    @PostMapping("/Modify/Anonce")
    public ResponseEntity<?> modifierAnonce(@RequestParam @NonNull Long id , @RequestBody ANONCE_DTO_PUBLIER DTO){
        return membreService.modifierAnonce(id,DTO);
    }

}
