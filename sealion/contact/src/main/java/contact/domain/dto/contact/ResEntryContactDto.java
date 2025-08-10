package contact.domain.dto.contact;

import java.util.UUID;
import lombok.Data;

@Data
public class ResEntryContactDto {
    
    
    private UUID id;

    private String businessName;

    private String internalName;

    private String detail;

    private String note;

    private String contactType;

    private String address;
    
    private String phone;

    private String mobilePhone;

    private String line;

    private String email;

    private String createdBy;

}
