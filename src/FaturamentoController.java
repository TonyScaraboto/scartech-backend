package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/faturamento")
public class FaturamentoController {
    @PostMapping("/venda")
    public ResponseEntity<String> registrarVenda(@RequestBody Map<String, Object> venda) {
        String scriptPath = "../backend/python/registrar_venda.py";
        String result = PythonRunner.runPythonScript(scriptPath, venda);
        return ResponseEntity.ok(result != null ? result : "Erro ao registrar venda");
    }

    @PostMapping("/ordem-status")
    public ResponseEntity<String> atualizarStatusOrdem(@RequestBody Map<String, Object> ordem) {
        String scriptPath = "../backend/python/atualizar_status_ordem.py";
        String result = PythonRunner.runPythonScript(scriptPath, ordem);
        return ResponseEntity.ok(result != null ? result : "Erro ao atualizar status");
    }
}
