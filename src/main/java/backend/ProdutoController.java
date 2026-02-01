package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.io.File;

@RestController
@RequestMapping("/api/acessorios")
@CrossOrigin(origins = "*")
public class ProdutoController {
    
    private File getPythonDir() {
        String basePath = System.getProperty("user.dir");
        File pythonDir = new File(basePath, "python");
        if (!pythonDir.exists()) {
            pythonDir = new File(basePath, "backend/python");
        }
        return pythonDir;
    }

    @PostMapping("/catalogar")
    public ResponseEntity<String> catalogarAcessorio(@RequestBody Map<String, Object> acessorio) {
        File pythonDir = getPythonDir();
        String scriptPath = new File(pythonDir, "catalogar_acessorio.py").getAbsolutePath();
        String result = PythonRunner.runPythonScript(scriptPath, acessorio);
        return ResponseEntity.ok(result != null ? result : "Erro ao catalogar acessório");
    }
}
