package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.application.port.in.ChargeBalanceUseCase;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balances")
@RequiredArgsConstructor
public class ChargeBalanceController {

    private final ChargeBalanceUseCase chargeBalanceUseCase;

    @PostMapping("/charge")
    public ResponseEntity<Response> chargeBalance(@RequestBody Request request) {
        ChargeBalanceCommand command = toCommand(request);
        Balance chargedBalance = chargeBalanceUseCase.chargeBalance(command);
        Response response = toResponse(chargedBalance);

        return ResponseEntity.ok(response);
    }

    private ChargeBalanceCommand toCommand(Request request) {
        return new ChargeBalanceCommand(
                new UserId(request.userId()),
                new BalanceAmount(request.amount())
        );
    }

    private Response toResponse(Balance balance) {
        return new Response(
                balance.getUserId().value(),
                balance.getAmount().value()
        );
    }

    public record Request(
            Long userId,
            Long amount
    ) {}

    public record Response(
            Long userId,
            Long amount
    ) {}
}