package backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        // Implementação de exemplo
        return ResponseEntity.ok("Status da ordem");
    }
}
