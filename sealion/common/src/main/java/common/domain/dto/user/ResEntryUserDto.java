package common.domain.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryUserDto {
    
    private UUID id;
    
    private String email;
    
    private String username;
    
    private String firstName;

    private String lastName;
    
    
    private LocalDateTime dob;
    
    // it object neet to call .getDetails() to get
    private String gender;
    private String role;



}
