package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.RankProductCommand;
import kr.hhplus.be.server.application.port.in.RankProductUseCase;
import kr.hhplus.be.server.application.port.out.product.AddProductSalesVolumePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankProductService implements RankProductUseCase {

    private final AddProductSalesVolumePort addProductSalesVolumePort;

    @Override
    public void rankProduct(RankProductCommand command) {
        addProductSalesVolumePort.addProductSalesVolume(
                command.quantities(),
                command.soldAt()
        );
    }
}
