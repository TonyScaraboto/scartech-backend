package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    @PostMapping("/catalogar")
    public ResponseEntity<String> catalogarProduto(@RequestBody Map<String, Object> produto) {
        String scriptPath = "../backend/python/catalogar_produto.py";
        String result = PythonRunner.runPythonScript(scriptPath, produto);
        return ResponseEntity.ok(result != null ? result : "Erro ao catalogar produto");
    }

    @PostMapping("/acessorio")
    public ResponseEntity<String> catalogarAcessorio(@RequestBody Map<String, Object> acessorio) {
        String scriptPath = "../backend/python/catalogar_acessorio.py";
        String result = PythonRunner.runPythonScript(scriptPath, acessorio);
        return ResponseEntity.ok(result != null ? result : "Erro ao catalogar acessório");
    }
}
