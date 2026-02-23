package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import backend.dto.Fatura;
import backend.dto.ErrorResponse;
import backend.service.FaturamentoService;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/faturamento")
@CrossOrigin(origins = "*")
public class FaturamentoController {
    private static final Logger logger = Logger.getLogger(FaturamentoController.class.getName());
    private final FaturamentoService faturamentoService;

    public FaturamentoController() {
        this.faturamentoService = new FaturamentoService();
    }

    /**
     * GET /api/faturamento/status
     * Retorna o status geral do sistema de faturamento
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        logger.info("GET /api/faturamento/status");
        return ResponseEntity.ok(Map.of(
            "status", "operacional",
            "versao", "1.0.0",
            "mensagem", "Sistema de faturamento funcionando corretamente"
        ));
    }

    /**
     * POST /api/faturamento
     * Cria uma nova fatura
     */
    @PostMapping
    public ResponseEntity<?> criarFatura(@RequestBody Fatura fatura) {
        try {
            logger.info("POST /api/faturamento - Criando nova fatura");
            
            if (fatura.getValor() == null || fatura.getValor() <= 0) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Valor inválido", "O valor deve ser maior que zero", 400)
                );
            }
            
            if (fatura.getDescricao() == null || fatura.getDescricao().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Descrição inválida", "A descrição é obrigatória", 400)
                );
            }
            
            Fatura novaFatura = faturamentoService.criarFatura(fatura);
            logger.info("Fatura criada com sucesso: " + novaFatura.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(novaFatura);
            
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao criar fatura", e);
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Dados inválidos", e.getMessage(), 400)
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao criar fatura", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao criar fatura", e.getMessage(), 500)
            );
        }
    }

    /**
     * GET /api/faturamento
     * Lista todas as faturas do mês/ano especificados
     */
    @GetMapping
    public ResponseEntity<?> listarFaturas(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        try {
            logger.info("GET /api/faturamento - Listando faturas (mes=" + mes + ", ano=" + ano + ")");
            List<Fatura> faturas = faturamentoService.listarFaturas(mes, ano);
            return ResponseEntity.ok(faturas);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao listar faturas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao listar faturas", e.getMessage(), 500)
            );
        }
    }

    /**
     * GET /api/faturamento/{id}
     * Obtém detalhes de uma fatura específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterFatura(@PathVariable String id) {
        try {
            logger.info("GET /api/faturamento/" + id);
            Fatura fatura = faturamentoService.obterFatura(id);
            return ResponseEntity.ok(fatura);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Fatura não encontrada: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse("Fatura não encontrada", "ID: " + id, 404)
            );
        }
    }

    /**
     * PUT /api/faturamento/{id}
     * Atualiza uma fatura existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFatura(@PathVariable String id, @RequestBody Fatura fatura) {
        try {
            logger.info("PUT /api/faturamento/" + id);
            
            if (fatura.getValor() != null && fatura.getValor() <= 0) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse("Valor inválido", "O valor deve ser maior que zero", 400)
                );
            }
            
            Fatura faturaAtualizada = faturamentoService.atualizarFatura(id, fatura);
            logger.info("Fatura atualizada: " + id);
            return ResponseEntity.ok(faturaAtualizada);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar fatura", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao atualizar fatura", e.getMessage(), 500)
            );
        }
    }

    /**
     * DELETE /api/faturamento/{id}
     * Deleta uma fatura
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFatura(@PathVariable String id) {
        try {
            logger.info("DELETE /api/faturamento/" + id);
            faturamentoService.deletarFatura(id);
            logger.info("Fatura deletada: " + id);
            return ResponseEntity.ok(Map.of("mensagem", "Fatura deletada com sucesso"));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar fatura", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao deletar fatura", e.getMessage(), 500)
            );
        }
    }

    /**
     * GET /api/faturamento/resumo/mensal
     * Retorna resumo de faturamento do mês/ano
     */
    @GetMapping("/resumo/mensal")
    public ResponseEntity<?> obterResumoMensal(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        try {
            logger.info("GET /api/faturamento/resumo/mensal (mes=" + mes + ", ano=" + ano + ")");
            Map<String, Object> resumo = faturamentoService.obterResumoMensal(mes, ano);
            return ResponseEntity.ok(resumo);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao obter resumo mensal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao obter resumo", e.getMessage(), 500)
            );
        }
    }

    /**
     * PATCH /api/faturamento/{id}/status
     * Atualiza apenas o status de uma fatura (pago, pendente, cancelado)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable String id,
            @RequestParam String status) {
        try {
            logger.info("PATCH /api/faturamento/" + id + "/status - novo status: " + status);
            
            if (!status.matches("^(pago|pendente|cancelado)$")) {
                return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                        "Status inválido",
                        "Status deve ser: pago, pendente ou cancelado",
                        400
                    )
                );
            }
            
            Fatura fatura = faturamentoService.obterFatura(id);
            fatura.setStatus(status);
            fatura = faturamentoService.atualizarFatura(id, fatura);
            logger.info("Status da fatura atualizado: " + id + " -> " + status);
            return ResponseEntity.ok(fatura);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("Erro ao atualizar status", e.getMessage(), 500)
            );
        }
    }
}
