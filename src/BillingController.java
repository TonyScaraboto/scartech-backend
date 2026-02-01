package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    @GetMapping("/summary")
    public ResponseEntity<String> getBillingSummary() {
        String scriptPath = "../backend/python/billing_summary.py";
        String result = PythonRunner.runPythonScript(scriptPath);
        return ResponseEntity.ok(result != null ? result : "Erro ao calcular faturamento");
    }
}
