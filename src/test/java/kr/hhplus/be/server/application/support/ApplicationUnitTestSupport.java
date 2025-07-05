package kr.hhplus.be.server.application.support;

import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.SaveBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.SaveCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.order.LoadOrderPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.order.UpdateOrderPort;
import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApplicationUnitTestSupport {
    // event
    @Mock public PublishEventPort publishEventPort;

    // balance
    @Mock public LoadBalancePort loadBalancePort;
    @Mock public SaveBalancePort saveBalancePort;
    @Mock public UpdateBalancePort updateBalancePort;

    // coupon
    @Mock public LoadCouponPort loadCouponPort;
    @Mock public SaveCouponPort saveCouponPort;
    @Mock public UpdateCouponPort updateCouponPort;

    // order
    @Mock public LoadOrderPort loadOrderPort;
    @Mock public SaveOrderPort saveOrderPort;
    @Mock public UpdateOrderPort updateOrderPort;

    // product
    @Mock public ListProductPort listProductPort;
    @Mock public LoadProductsPort loadProductsPort;
    @Mock public SaveProductPort saveProductPort;
    @Mock public UpdateProductsPort updateProductsPort;

}
