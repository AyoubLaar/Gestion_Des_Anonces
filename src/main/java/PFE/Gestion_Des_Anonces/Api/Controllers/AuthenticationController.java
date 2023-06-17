package PFE.Gestion_Des_Anonces.Api.Controllers;

import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.LOGIN_REQUEST_DTO;
import PFE.Gestion_Des_Anonces.Api.Models.User.User;
import PFE.Gestion_Des_Anonces.Api.Services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(path = "/api/Auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(
            @RequestBody @NonNull User request
    ) {
        return authenticationService.register(request);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String,String>> authenticate(
            @RequestBody @NonNull LOGIN_REQUEST_DTO request
    ) {
        System.out.println("Authentication requested");
        return authenticationService.authenticate(request);
    }
    @GetMapping("/VerifyToken")
    public ResponseEntity<Boolean> isUser(){
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/VerifyTokenAdmin")
    public ResponseEntity<Boolean> isAdmin(){
        return ResponseEntity.ok().body(true);
    }

}
