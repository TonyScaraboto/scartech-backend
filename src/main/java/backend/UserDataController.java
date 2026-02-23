package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import backend.dto.ErrorResponse;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class UserDataController {
    
    private static final Logger logger = Logger.getLogger(UserDataController.class.getName());
    private static final String DATA_DIR = "user_data";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public UserDataController() {
        // Criar diretório de dados se não existir
        try {
            new File(DATA_DIR).mkdirs();
            logger.info("Diretório de dados user_data criado/verificado");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao criar diretório de dados", e);
        }
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
     * GET /api/data/{userId}
     * Retorna todos os dados do usuário
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllData(@PathVariable String userId) {
        try {
            logger.info("GET /api/data/" + userId);
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter dados", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter dados", e.getMessage(), 500)
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro inesperado", e.getMessage(), 500)
            );
        }
    }
    
    // ========== ORDENS ==========
    /**
     * GET /api/data/{userId}/ordens
     * Retorna todas as ordens do usuário
     */
    @GetMapping("/{userId}/ordens")
    public ResponseEntity<?> getOrdens(@PathVariable String userId) {
        try {
            logger.info("GET /api/data/" + userId + "/ordens");
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("ordens", new ArrayList<>()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter ordens", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter ordens", e.getMessage(), 500)
            );
        }
    }
    
    /**
     * POST /api/data/{userId}/ordens
     * Substitui todas as ordens do usuário
     */
    @PostMapping("/{userId}/ordens")
    public ResponseEntity<?> saveOrdens(@PathVariable String userId, @RequestBody List<Map<String, Object>> ordens) {
        try {
            logger.info("POST /api/data/" + userId + "/ordens - Salvando " + ordens.size() + " ordens");
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
    
    /**
     * POST /api/data/{userId}/ordens/add
     * Adiciona uma nova ordem
     */
    @PostMapping("/{userId}/ordens/add")
    public ResponseEntity<?> addOrdem(@PathVariable String userId, @RequestBody Map<String, Object> ordem) {
        try {
            logger.info("POST /api/data/" + userId + "/ordens/add");
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
    /**
     * GET /api/data/{userId}/vendas
     * Retorna todas as vendas do usuário
     */
    @GetMapping("/{userId}/vendas")
    public ResponseEntity<?> getVendas(@PathVariable String userId) {
        try {
            logger.info("GET /api/data/" + userId + "/vendas");
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("vendas", new ArrayList<>()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter vendas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter vendas", e.getMessage(), 500)
            );
        }
    }
    
    /**
     * POST /api/data/{userId}/vendas
     * Substitui todas as vendas do usuário
     */
    @PostMapping("/{userId}/vendas")
    public ResponseEntity<?> saveVendas(@PathVariable String userId, @RequestBody List<Map<String, Object>> vendas) {
        try {
            logger.info("POST /api/data/" + userId + "/vendas - Salvando " + vendas.size() + " vendas");
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
    
    /**
     * POST /api/data/{userId}/vendas/add
     * Adiciona uma nova venda
     */
    @PostMapping("/{userId}/vendas/add")
    public ResponseEntity<?> addVenda(@PathVariable String userId, @RequestBody Map<String, Object> venda) {
        try {
            logger.info("POST /api/data/" + userId + "/vendas/add");
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
    /**
     * GET /api/data/{userId}/produtos
     * Retorna todos os produtos do usuário
     */
    @GetMapping("/{userId}/produtos")
    public ResponseEntity<?> getProdutos(@PathVariable String userId) {
        try {
            logger.info("GET /api/data/" + userId + "/produtos");
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("produtos", new ArrayList<>()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao obter produtos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter produtos", e.getMessage(), 500)
            );
        }
    }
    
    /**
     * POST /api/data/{userId}/produtos
     * Substitui todos os produtos do usuário
     */
    @PostMapping("/{userId}/produtos")
    public ResponseEntity<?> saveProdutos(@PathVariable String userId, @RequestBody List<Map<String, Object>> produtos) {
        try {
            logger.info("POST /api/data/" + userId + "/produtos - Salvando " + produtos.size() + " produtos");
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
    
    /**
     * POST /api/data/{userId}/produtos/add
     * Adiciona um novo produto
     */
    @PostMapping("/{userId}/produtos/add")
    public ResponseEntity<?> addProduto(@PathVariable String userId, @RequestBody Map<String, Object> produto) {
        try {
            logger.info("POST /api/data/" + userId + "/produtos/add");
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
    /**
     * POST /api/data/{userId}/sync
     * Sincroniza todos os dados do usuário
     */
    @PostMapping("/{userId}/sync")
    public ResponseEntity<?> syncData(@PathVariable String userId, @RequestBody Map<String, Object> allData) {
        try {
            logger.info("POST /api/data/" + userId + "/sync - Sincronizando dados");
            if (allData == null || allData.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados vazios", "Nenhum dado para sincronizar", 400)
                );
            }
            saveUserData(userId, allData);
            return ResponseEntity.ok(Map.of("success", true, "message", "Dados sincronizados com sucesso"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao sincronizar dados", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao sincronizar dados", e.getMessage(), 500)
            );
        }
    }
}
