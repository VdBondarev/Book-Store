package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.payment.PaymentResponseDto;
import book.store.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toResponseDto(Payment payment);
}
