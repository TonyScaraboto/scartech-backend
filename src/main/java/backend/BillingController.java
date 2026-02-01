package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.nio.file.Paths;
import java.io.File;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingController {
    @GetMapping("/summary")
    public ResponseEntity<String> getBillingSummary() {
        String basePath = System.getProperty("user.dir");
        // Verifica se está rodando da pasta backend ou da pasta raiz
        File pythonDir = new File(basePath, "python");
        if (!pythonDir.exists()) {
            pythonDir = new File(basePath, "backend/python");
        }
        String scriptPath = new File(pythonDir, "billing_summary.py").getAbsolutePath();
        String workingDir = pythonDir.getAbsolutePath();
        String result = PythonRunner.runPythonScript(scriptPath, workingDir);
        return ResponseEntity.ok(result != null ? result : "Erro ao calcular faturamento");
    }
}
