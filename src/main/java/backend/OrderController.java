package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.io.File;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    private File getPythonDir() {
        String basePath = System.getProperty("user.dir");
        File pythonDir = new File(basePath, "python");
        if (!pythonDir.exists()) {
            pythonDir = new File(basePath, "backend/python");
        }
        return pythonDir;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Status da ordem");
    }

    @PostMapping("/generate-pdf")
    public ResponseEntity<String> generateOrderPdf(@RequestBody Map<String, Object> orderData) {
        try {
            File pythonDir = getPythonDir();
            String scriptPath = new File(pythonDir, "pdf_generator.py").getAbsolutePath();
            String workingDir = pythonDir.getAbsolutePath();
            
            String result = PythonRunner.runPythonScript(scriptPath, orderData);
            if (result != null && result.contains("sucesso")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body("Erro ao gerar PDF: " + (result != null ? result : "Script não retornou resultado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}
