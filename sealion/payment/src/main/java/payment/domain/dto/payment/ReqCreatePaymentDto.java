package payment.domain.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.dto.item.ReqCreatePaymentItemDto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreatePaymentDto {

    @NotBlank(message = "Payment name is required")
    private String note;

    private String paymentDate;

    private UUID contact;

    private UUID property;

    private List<ReqCreatePaymentItemDto> items;

    public LocalDateTime getPersistPaymentDate() {
        try {
            if (this.paymentDate == null || this.paymentDate.isBlank()) {
                return null;
            }
            // Parse the string with timezone info (Z for UTC) into a ZonedDateTime
            // and then convert it to a LocalDateTime.
            return ZonedDateTime.parse(this.paymentDate).toLocalDateTime();
        } catch (DateTimeParseException e) {
            return null;
        }

    }


}
