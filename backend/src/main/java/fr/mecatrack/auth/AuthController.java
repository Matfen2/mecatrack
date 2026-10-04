package fr.mecatrack.auth;

import fr.mecatrack.api.AuthApi;
import fr.mecatrack.api.dto.LoginRequestDto;
import fr.mecatrack.api.dto.LoginResponseDto;
import fr.mecatrack.api.dto.RoleDto;
import fr.mecatrack.api.dto.UtilisateurResumeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implémente l'interface AuthApi générée depuis openapi.yaml :
 * la route, la méthode HTTP et la validation viennent du contrat.
 */
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        var resultat = authService.connecter(loginRequestDto.getEmail(), loginRequestDto.getMotDePasse());
        var utilisateur = resultat.utilisateur();

        var resume = new UtilisateurResumeDto()
                .idUtilisateur(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(RoleDto.fromValue(utilisateur.getRole().name()));

        var reponse = new LoginResponseDto()
                .token(resultat.jeton().valeur())
                .expireDans((int) resultat.jeton().expireDansSecondes())
                .utilisateur(resume);

        return ResponseEntity.ok(reponse);
    }
}