package com.example.practice_3.web;

import com.example.practice_3.delivery.DeliveryQuote;
import com.example.practice_3.delivery.DeliveryService;
import com.example.practice_3.web.dto.QuoteRecordDto;
import com.example.practice_3.web.dto.QuoteRequest;
import com.example.practice_3.web.dto.QuoteResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotes")
public class DeliveryQuoteController {

    private final DeliveryService deliveryService;

    public DeliveryQuoteController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * POST /api/v1/quotes
     * Рассчитать стоимость доставки и сохранить в историю.
     */
    @PostMapping
    public ResponseEntity<QuoteResponse> quote(
            @Valid @RequestBody QuoteRequest req
    ) {
        DeliveryQuote quote = deliveryService.quote(req.region(), req.amount());
        return ResponseEntity.ok(QuoteResponse.from(quote));
    }

    /**
     * GET /api/v1/quotes?region=ALA&page=0&size=20&sort=quotedAt,desc
     * История расчётов с пагинацией и сортировкой.
     */
    @GetMapping
    public Page<QuoteRecordDto> list(
            @RequestParam(required = false) String region,
            @PageableDefault(size = 20, sort = "quotedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return deliveryService.listQuotes(region, pageable);
    }
}
