package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.io.File;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    @PostMapping("/generate-pdf")
    public ResponseEntity<String> generateOrderPdf(@RequestBody Map<String, Object> orderData) {
        try {
            // Caminho absoluto para o script Python
            String basePath = System.getProperty("user.dir");
            String scriptPath = basePath + File.separator + "python" + File.separator + "pdf_generator.py";
            
            // Se estiver rodando de backend/target, ajusta o caminho
            if (!new File(scriptPath).exists()) {
                scriptPath = basePath + File.separator + ".." + File.separator + "python" + File.separator + "pdf_generator.py";
            }
            
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
