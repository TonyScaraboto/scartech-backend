package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import backend.dto.UserCredentials;
import backend.dto.AuthToken;
import backend.dto.User;
import backend.dto.ErrorResponse;
import backend.service.UserAuthService;
import backend.util.JwtUtil;

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getName());
    private final UserAuthService userAuthService;

    public AuthenticationController() {
        this.userAuthService = new UserAuthService();
    }

    /**
     * POST /api/auth/register
     * Registra um novo usuário
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody UserCredentials credentials) {
        try {
            logger.info("POST /api/auth/register - Registrando novo usuário");

            if (credentials.getEmail() == null || credentials.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Email obrigatório", "O email é necessário para registro", 400)
                );
            }

            if (!credentials.getEmail().contains("@")) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Email inválido", "Formato de email inválido", 400)
                );
            }

            User novoUsuario = userAuthService.registrarUsuario(credentials.getEmail(), credentials.getPassword());
            logger.info("Usuário registrado com sucesso: " + novoUsuario.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensagem", "Usuário registrado com sucesso",
                "userId", novoUsuario.getUserId(),
                "email", novoUsuario.getEmail()
            ));

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação no registro", e);
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Dados inválidos", e.getMessage(), 400)
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao registrar usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao registrar", e.getMessage(), 500)
            );
        }
    }

    /**
     * POST /api/auth/login
     * Autentica um usuário e retorna um token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCredentials credentials) {
        try {
            logger.info("POST /api/auth/login - Autenticando usuário");

            if (credentials.getEmail() == null || credentials.getPassword() == null) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                        "Credenciais incompletas",
                        "Email e senha são obrigatórios",
                        400
                    )
                );
            }

            String token = userAuthService.autenticar(credentials.getEmail(), credentials.getPassword());
            String userId = JwtUtil.extractUserId(token);

            AuthToken authToken = new AuthToken(
                token,
                userId,
                credentials.getEmail(),
                JwtUtil.getExpirationTimeSeconds()
            );

            logger.info("Usuário autenticado com sucesso: " + credentials.getEmail());
            return ResponseEntity.ok(authToken);

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Falha de autenticação", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("Autenticação falhou", e.getMessage(), 401)
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao fazer login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao fazer login", e.getMessage(), 500)
            );
        }
    }

    /**
     * POST /api/auth/logout
     * Realiza logout (invalidação do token no cliente)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            logger.info("POST /api/auth/logout - Usuário fazendo logout");

            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String userId = JwtUtil.extractUserId(jwt);
                logger.info("Logout realizado para usuário: " + userId);
            }

            return ResponseEntity.ok(Map.of(
                "mensagem", "Logout realizado com sucesso"
            ));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao fazer logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao fazer logout", e.getMessage(), 500)
            );
        }
    }

    /**
     * GET /api/auth/verify
     * Verifica se um token é válido
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verificarToken(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            logger.info("GET /api/auth/verify - Verificando token");

            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse("Token não fornecido", "Adicione o header Authorization", 401)
                );
            }

            String jwt = token.substring(7);
            if (!userAuthService.validarToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse("Token inválido", "O token fornecido é inválido ou expirou", 401)
                );
            }

            String userId = userAuthService.extrairUserId(jwt);
            String email = userAuthService.extrairEmail(jwt);

            logger.info("Token válido para usuário: " + email);
            return ResponseEntity.ok(Map.of(
                "valido", true,
                "userId", userId,
                "email", email
            ));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao verificar token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao verificar token", e.getMessage(), 500)
            );
        }
    }

    /**
     * GET /api/auth/me
     * Retorna informações do usuário autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> obterPerfilUsuario(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            logger.info("GET /api/auth/me - Obtendo perfil do usuário");

            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse("Token não fornecido", "Adicione o header Authorization", 401)
                );
            }

            String jwt = token.substring(7);
            if (!userAuthService.validarToken(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse("Token inválido", "O token fornecido é inválido ou expirou", 401)
                );
            }

            String userId = userAuthService.extrairUserId(jwt);
            User usuario = userAuthService.obterUsuario(userId);

            logger.info("Perfil obtido para: " + usuario.getEmail());
            return ResponseEntity.ok(Map.of(
                "userId", usuario.getUserId(),
                "email", usuario.getEmail(),
                "createdAt", usuario.getCreatedAt()
            ));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao obter perfil", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter perfil", e.getMessage(), 500)
            );
        }
    }
}
