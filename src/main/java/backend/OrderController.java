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
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private static final Logger logger = Logger.getLogger(OrderController.class.getName());

    /**
     * GET /api/orders/status
     * Retorna o status geral do serviço de ordens
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        logger.info("GET /api/orders/status");
        return ResponseEntity.ok(Map.of(
            "status", "operacional",
            "mensagem", "Serviço de ordens funcionando corretamente"
        ));
    }

    /**
     * POST /api/orders/generate-pdf
     * Gera PDF da ordem de serviço
     */
    @PostMapping("/generate-pdf")
    public ResponseEntity<?> generateOrderPdf(@RequestBody Map<String, Object> orderData) {
        try {
            logger.info("POST /api/orders/generate-pdf - Gerando PDF para ordem");

            if (orderData == null || orderData.isEmpty()) {
                logger.warning("Dados da ordem vazios");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Os dados da ordem são obrigatórios", 400)
                );
            }

            String nomeCliente = (String) orderData.get("nomeCliente");
            if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
                logger.warning("Nome do cliente não fornecido");
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Nome do cliente é obrigatório", 400)
                );
            }

            try {
                String scriptPath = FileUtil.getPythonScriptPath("pdf_generator.py");
                File pythonDir = FileUtil.getPythonDirectory();
                String result = PythonRunner.runPythonScript(scriptPath, orderData);

                if (result != null && result.contains("sucesso")) {
                    logger.info("PDF gerado com sucesso: " + result);
                    return ResponseEntity.ok(Map.of(
                        "mensagem", result,
                        "status", "sucesso"
                    ));
                } else {
                    logger.warning("Erro ao gerar PDF: " + result);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ErrorResponse(
                            "Erro ao gerar PDF",
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
                        "pdf_generator.py não foi localizado",
                        500
                    )
                );
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao gerar PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao gerar PDF", e.getMessage(), 500)
            );
        }
    }

    /**
     * POST /api/orders/validate
     * Valida os dados de uma ordem antes de processar
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateOrder(@RequestBody Map<String, Object> orderData) {
        try {
            logger.info("POST /api/orders/validate - Validando ordem");

            if (orderData == null || orderData.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Dados inválidos", "Os dados da ordem são obrigatórios", 400)
                );
            }

            // Validações básicas
            String[] camposObrigatorios = {"nomeCliente", "documentoCliente", "telefoneCliente", 
                                          "modeloAparelho", "defeitoApresentado", "valorConserto"};
            
            for (String campo : camposObrigatorios) {
                if (!orderData.containsKey(campo) || orderData.get(campo) == null) {
                    return ResponseEntity.badRequest().body(
                        new ErrorResponse("Campo obrigatório faltando", "Campo: " + campo, 400)
                    );
                }
            }

            // Validar valor numérico
            Object valor = orderData.get("valorConserto");
            if (valor != null) {
                try {
                    double v = Double.parseDouble(valor.toString());
                    if (v < 0) {
                        return ResponseEntity.badRequest().body(
                            new ErrorResponse("Valor inválido", "Valor não pode ser negativo", 400)
                        );
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(
                        new ErrorResponse("Valor inválido", "valorConserto deve ser um número", 400)
                    );
                }
            }

            logger.info("Ordem validada com sucesso");
            return ResponseEntity.ok(Map.of(
                "valido", true,
                "mensagem", "Ordem validada com sucesso"
            ));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao validar ordem", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao validar ordem", e.getMessage(), 500)
            );
        }
    }
}
