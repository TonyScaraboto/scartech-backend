package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import backend.dto.ErrorResponse;
import backend.service.UserAuthService;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller para dados do usuário com autenticação JWT
 * Todos os endpoints requerem um token válido no header Authorization
 */
@RestController
@RequestMapping("/api/user-data")
@CrossOrigin(origins = "*")
public class UserDataControllerAuth {
    
    private static final Logger logger = Logger.getLogger(UserDataControllerAuth.class.getName());
    private static final String DATA_DIR = "user_data";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserAuthService userAuthService;
    
    public UserDataControllerAuth() {
        this.userAuthService = new UserAuthService();
        try {
            new File(DATA_DIR).mkdirs();
            logger.info("Diretório de dados user_data criado/verificado");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório de dados", e);
        }
    }

    /**
     * Extrai e valida o token JWT do header Authorization
     * @return userId se válido, null se inválido
     */
    private String extrairUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Token não fornecido no header Authorization");
            return null;
        }

        String token = authHeader.substring(7);
        if (!userAuthService.validarToken(token)) {
            logger.warning("Token inválido ou expirado");
            return null;
        }

        String userId = userAuthService.extrairUserId(token);
        logger.info("Acesso autorizado para usuário: " + userId);
        return userId;
    }

    /**
     * Resposta de erro de autenticação
     */
    private ResponseEntity<?> erroAutenticacao() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse(
                "Autenticação requerida",
                "Adicione um token válido no header: Authorization: Bearer {token}",
                401
            )
        );
    }
    
    private File getUserFile(String userId) {
        return new File(DATA_DIR, userId + ".json");
    }
    
    private Map<String, Object> loadUserData(String userId) throws IOException {
        try {
            File file = getUserFile(userId);
            if (file.exists()) {
                logger.info("Carregando dados do usuário: " + userId);
                return objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {});
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar dados do usuário: " + userId, e);
            throw e;
        }
        // Retornar estrutura padrão
        Map<String, Object> data = new HashMap<>();
        data.put("ordens", new ArrayList<>());
        data.put("vendas", new ArrayList<>());
        data.put("produtos", new ArrayList<>());
        return data;
    }
    
    private void saveUserData(String userId, Map<String, Object> data) throws IOException {
        try {
            File file = getUserFile(userId);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            logger.info("Dados salvos para usuário: " + userId);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar dados do usuário: " + userId, e);
            throw e;
        }
    }

    // ========== GET ALL DATA ==========
    /**
     * GET /api/user-data
     * Retorna todos os dados do usuário autenticado
     */
    @GetMapping
    public ResponseEntity<?> getAllData(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            logger.info("GET /api/user-data");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter dados", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter dados", e.getMessage(), 500)
            );
        }
    }
    
    // ========== ORDENS ==========
    @GetMapping("/ordens")
    public ResponseEntity<?> getOrdens(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            logger.info("GET /api/user-data/ordens");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("ordens", new ArrayList<>()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter ordens", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter ordens", e.getMessage(), 500)
            );
        }
    }
    
    @PostMapping("/ordens")
    public ResponseEntity<?> saveOrdens(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @RequestBody List<Map<String, Object>> ordens) {
        try {
            logger.info("POST /api/user-data/ordens");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            data.put("ordens", ordens);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Ordens salvas com sucesso", "quantidade", ordens.size()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar ordens", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao salvar ordens", e.getMessage(), 500)
            );
        }
    }
    
    @PostMapping("/ordens/add")
    public ResponseEntity<?> addOrdem(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                     @RequestBody Map<String, Object> ordem) {
        try {
            logger.info("POST /api/user-data/ordens/add");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            if (ordem == null || ordem.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Ordem inválida", "Os dados da ordem são obrigatórios", 400)
                );
            }
            Map<String, Object> data = loadUserData(userId);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> ordens = (List<Map<String, Object>>) data.getOrDefault("ordens", new ArrayList<>());
            ordens.add(ordem);
            data.put("ordens", ordens);
            saveUserData(userId, data);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "Ordem adicionada"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao adicionar ordem", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao adicionar ordem", e.getMessage(), 500)
            );
        }
    }
    
    // ========== VENDAS ==========
    @GetMapping("/vendas")
    public ResponseEntity<?> getVendas(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            logger.info("GET /api/user-data/vendas");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("vendas", new ArrayList<>()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter vendas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter vendas", e.getMessage(), 500)
            );
        }
    }
    
    @PostMapping("/vendas")
    public ResponseEntity<?> saveVendas(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @RequestBody List<Map<String, Object>> vendas) {
        try {
            logger.info("POST /api/user-data/vendas");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            data.put("vendas", vendas);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Vendas salvas com sucesso", "quantidade", vendas.size()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar vendas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao salvar vendas", e.getMessage(), 500)
            );
        }
    }
    
    @PostMapping("/vendas/add")
    public ResponseEntity<?> addVenda(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                     @RequestBody Map<String, Object> venda) {
        try {
            logger.info("POST /api/user-data/vendas/add");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            if (venda == null || venda.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Venda inválida", "Os dados da venda são obrigatórios", 400)
                );
            }
            Map<String, Object> data = loadUserData(userId);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> vendas = (List<Map<String, Object>>) data.getOrDefault("vendas", new ArrayList<>());
            vendas.add(venda);
            data.put("vendas", vendas);
            saveUserData(userId, data);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "Venda adicionada"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao adicionar venda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao adicionar venda", e.getMessage(), 500)
            );
        }
    }
    
    // ========== PRODUTOS ==========
    @GetMapping("/produtos")
    public ResponseEntity<?> getProdutos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            logger.info("GET /api/user-data/produtos");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("produtos", new ArrayList<>()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter produtos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter produtos", e.getMessage(), 500)
            );
        }
    }
    
    @PostMapping("/produtos")
    public ResponseEntity<?> saveProdutos(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @RequestBody List<Map<String, Object>> produtos) {
        try {
            logger.info("POST /api/user-data/produtos");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            Map<String, Object> data = loadUserData(userId);
            data.put("produtos", produtos);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Produtos salvos com sucesso", "quantidade", produtos.size()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar produtos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao salvar produtos", e.getMessage(), 500)
            );
        }
    }
    
    @PostMapping("/produtos/add")
    public ResponseEntity<?> addProduto(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @RequestBody Map<String, Object> produto) {
        try {
            logger.info("POST /api/user-data/produtos/add");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            if (produto == null || produto.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Produto inválido", "Os dados do produto são obrigatórios", 400)
                );
            }
            Map<String, Object> data = loadUserData(userId);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> produtos = (List<Map<String, Object>>) data.getOrDefault("produtos", new ArrayList<>());
            produtos.add(produto);
            data.put("produtos", produtos);
            saveUserData(userId, data);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "Produto adicionado"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao adicionar produto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao adicionar produto", e.getMessage(), 500)
            );
        }
    }
    
    // ========== SYNC (salvar tudo de uma vez) ==========
    @PostMapping("/sync")
    public ResponseEntity<?> syncData(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                     @RequestBody Map<String, Object> allData) {
        try {
            logger.info("POST /api/user-data/sync");
            String userId = extrairUserId(authHeader);
            if (userId == null) return erroAutenticacao();

            if (allData == null || allData.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados vazios", "Nenhum dado para sincronizar", 400)
                );
            }
            saveUserData(userId, allData);
            return ResponseEntity.ok(Map.of("success", true, "message", "Dados sincronizados com sucesso. Seus dados estão disponíveis em qualquer dispositivo."));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao sincronizar dados", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao sincronizar dados", e.getMessage(), 500)
            );
        }
    }
}
