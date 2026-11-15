package com.example.practice_3.delivery.web;

import com.example.practice_3.delivery.app.DeliveryService;
import com.example.practice_3.delivery.domain.DeliveryQuote;
import com.example.practice_3.delivery.web.dto.QuoteRecordDto;
import com.example.practice_3.delivery.web.dto.QuoteRequest;
import com.example.practice_3.delivery.web.dto.QuoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Delivery Quotes", description = "Управление расчётами условий и истории доставки")
@RestController
@RequestMapping("/api/v1/quotes")
public class DeliveryQuoteController {

    private final DeliveryService deliveryService;
    private final QuoteApiMapper quoteApiMapper;

    public DeliveryQuoteController(DeliveryService deliveryService, QuoteApiMapper quoteApiMapper) {
        this.deliveryService = deliveryService;
        this.quoteApiMapper = quoteApiMapper;
    }

    /**
     * POST /api/v1/quotes
     * Рассчитать стоимость доставки и сохранить в историю.
     */
    @Operation(summary = "Рассчитать условия доставки для заказа")
    @ApiResponse(responseCode = "200", description = "Успешный расчёт доставки")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Регион не обслуживается сервисом",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<QuoteResponse> quote(
            @Valid @RequestBody QuoteRequest req
    ) {
        DeliveryQuote quote = deliveryService.quote(req.region(), req.amount());
        return ResponseEntity.ok(quoteApiMapper.toResponse(quote));
    }

    /**
     * GET /api/v1/quotes?region=ALA&page=0&size=20&sort=quotedAt,desc
     * История расчётов с пагинацией и сортировкой.
     */
    @Operation(summary = "Получить историю расчётов (с кэшированием и пагинацией)")
    @ApiResponse(responseCode = "200", description = "Страница с историей расчётов")
    @GetMapping
    public Page<QuoteRecordDto> list(
            @Parameter(description = "Код региона для фильтрации (необязательно)", example = "ALA")
            @RequestParam(required = false) String region,
            @PageableDefault(size = 20, sort = "quotedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return deliveryService.listQuotes(region, pageable);
    }
}
