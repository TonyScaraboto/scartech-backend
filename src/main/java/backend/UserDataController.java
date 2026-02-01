package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.*;
import java.util.*;
import java.nio.file.*;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class UserDataController {
    
    private static final String DATA_DIR = "user_data";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public UserDataController() {
        // Criar diretório de dados se não existir
        new File(DATA_DIR).mkdirs();
    }
    
    private File getUserFile(String userId) {
        return new File(DATA_DIR, userId + ".json");
    }
    
    private Map<String, Object> loadUserData(String userId) {
        try {
            File file = getUserFile(userId);
            if (file.exists()) {
                return objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Retornar estrutura padrão
        Map<String, Object> data = new HashMap<>();
        data.put("ordens", new ArrayList<>());
        data.put("vendas", new ArrayList<>());
        data.put("produtos", new ArrayList<>());
        return data;
    }
    
    private void saveUserData(String userId, Map<String, Object> data) throws IOException {
        File file = getUserFile(userId);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
    }
    
    // ========== GET ALL DATA ==========
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getAllData(@PathVariable String userId) {
        try {
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== ORDENS ==========
    @GetMapping("/{userId}/ordens")
    public ResponseEntity<?> getOrdens(@PathVariable String userId) {
        try {
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("ordens", new ArrayList<>()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/ordens")
    public ResponseEntity<?> saveOrdens(@PathVariable String userId, @RequestBody List<Map<String, Object>> ordens) {
        try {
            Map<String, Object> data = loadUserData(userId);
            data.put("ordens", ordens);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Ordens salvas com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/ordens/add")
    public ResponseEntity<?> addOrdem(@PathVariable String userId, @RequestBody Map<String, Object> ordem) {
        try {
            Map<String, Object> data = loadUserData(userId);
            List<Map<String, Object>> ordens = (List<Map<String, Object>>) data.getOrDefault("ordens", new ArrayList<>());
            ordens.add(ordem);
            data.put("ordens", ordens);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Ordem adicionada"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== VENDAS ==========
    @GetMapping("/{userId}/vendas")
    public ResponseEntity<?> getVendas(@PathVariable String userId) {
        try {
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("vendas", new ArrayList<>()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/vendas")
    public ResponseEntity<?> saveVendas(@PathVariable String userId, @RequestBody List<Map<String, Object>> vendas) {
        try {
            Map<String, Object> data = loadUserData(userId);
            data.put("vendas", vendas);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Vendas salvas com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/vendas/add")
    public ResponseEntity<?> addVenda(@PathVariable String userId, @RequestBody Map<String, Object> venda) {
        try {
            Map<String, Object> data = loadUserData(userId);
            List<Map<String, Object>> vendas = (List<Map<String, Object>>) data.getOrDefault("vendas", new ArrayList<>());
            vendas.add(venda);
            data.put("vendas", vendas);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Venda adicionada"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== PRODUTOS ==========
    @GetMapping("/{userId}/produtos")
    public ResponseEntity<?> getProdutos(@PathVariable String userId) {
        try {
            Map<String, Object> data = loadUserData(userId);
            return ResponseEntity.ok(data.getOrDefault("produtos", new ArrayList<>()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/produtos")
    public ResponseEntity<?> saveProdutos(@PathVariable String userId, @RequestBody List<Map<String, Object>> produtos) {
        try {
            Map<String, Object> data = loadUserData(userId);
            data.put("produtos", produtos);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Produtos salvos com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/produtos/add")
    public ResponseEntity<?> addProduto(@PathVariable String userId, @RequestBody Map<String, Object> produto) {
        try {
            Map<String, Object> data = loadUserData(userId);
            List<Map<String, Object>> produtos = (List<Map<String, Object>>) data.getOrDefault("produtos", new ArrayList<>());
            produtos.add(produto);
            data.put("produtos", produtos);
            saveUserData(userId, data);
            return ResponseEntity.ok(Map.of("success", true, "message", "Produto adicionado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== SYNC (salvar tudo de uma vez) ==========
    @PostMapping("/{userId}/sync")
    public ResponseEntity<?> syncData(@PathVariable String userId, @RequestBody Map<String, Object> allData) {
        try {
            saveUserData(userId, allData);
            return ResponseEntity.ok(Map.of("success", true, "message", "Dados sincronizados com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
