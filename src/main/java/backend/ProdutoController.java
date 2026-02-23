package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import backend.util.FileUtil;
import backend.dto.ErrorResponse;

import java.util.Map;
import java.io.File;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/acessorios")
@CrossOrigin(origins = "*")
public class ProdutoController {
    private static final Logger logger = Logger.getLogger(ProdutoController.class.getName());

    /**
     * POST /api/acessorios/catalogar
     * Cataloga um novo acessório no sistema
     */
    @PostMapping("/catalogar")
    public ResponseEntity<?> catalogarAcessorio(@RequestBody Map<String, Object> acessorio) {
        try {
            logger.info("POST /api/acessorios/catalogar - Catalogando novo acessório");

            if (acessorio == null || acessorio.isEmpty()) {
                logger.warning("Dados do acessório vazios");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Os dados do acessório são obrigatórios", 400)
                );
            }

            String nome = (String) acessorio.get("nome");
            if (nome == null || nome.trim().isEmpty()) {
                logger.warning("Nome do acessório não fornecido");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Nome do acessório é obrigatório", 400)
                );
            }

            try {
                String scriptPath = FileUtil.getPythonScriptPath("catalogar_acessorio.py");
                File pythonDir = FileUtil.getPythonDirectory();
                String result = PythonRunner.runPythonScript(scriptPath, acessorio);

                if (result != null && !result.isEmpty()) {
                    logger.info("Acessório catalogado com sucesso: " + result);
                    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "mensagem", result,
                        "status", "sucesso",
                        "acessorio", acessorio
                    ));
                } else {
                    logger.warning("Erro ao catalogar acessório: " + result);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ErrorResponse(
                            "Erro ao catalogar",
                            result != null ? result : "Script não retornou resultado",
                            500
                        )
                    );
                }
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "Erro ao localizar script Python", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(
                        "Script não encontrado",
                        "catalogar_acessorio.py não foi localizado",
                        500
                    )
                );
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao catalogar acessório", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao catalogar acessório", e.getMessage(), 500)
            );
        }
    }

    /**
     * POST /api/acessorios/catalogar-produto
     * Cataloga um novo produto no sistema
     */
    @PostMapping("/catalogar-produto")
    public ResponseEntity<?> catalogarProduto(@RequestBody Map<String, Object> produto) {
        try {
            logger.info("POST /api/acessorios/catalogar-produto - Catalogando novo produto");

            if (produto == null || produto.isEmpty()) {
                logger.warning("Dados do produto vazios");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Os dados do produto são obrigatórios", 400)
                );
            }

            String nome = (String) produto.get("nome");
            if (nome == null || nome.trim().isEmpty()) {
                logger.warning("Nome do produto não fornecido");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Nome do produto é obrigatório", 400)
                );
            }

            try {
                String scriptPath = FileUtil.getPythonScriptPath("catalogar_produto.py");
                File pythonDir = FileUtil.getPythonDirectory();
                String result = PythonRunner.runPythonScript(scriptPath, produto);

                if (result != null && !result.isEmpty()) {
                    logger.info("Produto catalogado com sucesso: " + result);
                    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "mensagem", result,
                        "status", "sucesso",
                        "produto", produto
                    ));
                } else {
                    logger.warning("Erro ao catalogar produto: " + result);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ErrorResponse(
                            "Erro ao catalogar",
                            result != null ? result : "Script não retornou resultado",
                            500
                        )
                    );
                }
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "Erro ao localizar script Python", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(
                        "Script não encontrado",
                        "catalogar_produto.py não foi localizado",
                        500
                    )
                );
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao catalogar produto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao catalogar produto", e.getMessage(), 500)
            );
        }
    }
}
