package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import backend.service.FaturamentoService;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingController {
    private static final Logger logger = Logger.getLogger(BillingController.class.getName());
    private final FaturamentoService faturamentoService;

    public BillingController() {
        this.faturamentoService = new FaturamentoService();
    }

    /**
     * GET /api/billing/summary
     * Retorna o resumo de faturamento do mês atual
     * Mantido para compatibilidade com chamadas legadas
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getBillingSummary() {
        try {
            logger.info("GET /api/billing/summary");
            Map<String, Object> resumo = faturamentoService.obterResumoMensal(null, null);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao obter resumo de faturamento", e);
            return ResponseEntity.status(500).body(
                Map.of("erro", "Erro ao calcular faturamento", "mensagem", e.getMessage())
            );
        }
    }
}
