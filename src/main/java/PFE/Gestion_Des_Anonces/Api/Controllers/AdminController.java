package PFE.Gestion_Des_Anonces.Api.Controllers;

import PFE.Gestion_Des_Anonces.Api.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @GetMapping("/Anonces")
    public ResponseEntity<?> getAnonces(){
        return adminService.getAnonces();
    }

    @GetMapping("/Anonce")
    public ResponseEntity<?> getAnonce(@RequestParam Long id){
        return adminService.getAnonce(id);
    }

    @GetMapping("/Users")
    public ResponseEntity<?> getUsers(){
        return adminService.getUsers();
    }
    @GetMapping("/User")
    public ResponseEntity<?> getUser(@RequestParam Long id){
        return adminService.getUser(id);
    }

    @PostMapping("/AddCategorie")
    public ResponseEntity<?> addCategorie(@RequestBody String categorie){
        return adminService.addCategorie(categorie);
    }

    @PostMapping("/AddVille")
    public ResponseEntity<?> addVille(@RequestBody String ville, @RequestBody String region){
        return adminService.addVille(ville,region);
    }

    @PostMapping("/Anonce/toggle")
    public ResponseEntity<?> toggleAnonce(@RequestParam Long id){
        return adminService.toggleAnonce(id);
    }

    @PostMapping("/Anonce/supprimer")
    public ResponseEntity<?> supprimerAnonce(@RequestParam Long id){
        return adminService.supprimerAnonce(id);
    }

    @PostMapping("/User/toggle")
    public ResponseEntity<?> toggleUser(@RequestParam Long id){
        return adminService.toggleUser(id);
    }

    @PostMapping("/User/supprimer")
    public ResponseEntity<?> supprimerUser(@RequestParam Long id){
        return adminService.supprimerUser(id);
    }
}
