package common.domain.mapper;


import common.domain.entity.Gender;
import common.domain.entity.Role;
import common.domain.entity.User;

import common.domain.dto.user.ResEntryUserDto;
import org.mapstruct.*;


@Mapper(componentModel = "cdi")
public interface UserMapper {
    @Mapping(source = "gender", target = "gender", qualifiedByName = "genderToString")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    ResEntryUserDto toDto(User user);

    @Named("genderToString")
    static String genderToString(Gender gender) {
        return gender != null ? gender.getDetail() : null;
    }

    @Named("roleToString")
    static String roleToString(Role role) {
        return role != null ? role.getDetail() : null;
    }
}
