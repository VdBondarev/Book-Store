package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.UserAdminResponseDto;
import book.store.dto.UserRegistrationRequestDto;
import book.store.dto.UserResponseDto;
import book.store.dto.UserUpdateRequestDto;
import book.store.model.Role;
import book.store.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toResponseDto(User user);

    @Mapping(target = "rolesIds", ignore = true)
    UserAdminResponseDto toAdminResponseDto(User user);

    @Mapping(target = "password", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    void toModel(@MappingTarget User user, UserUpdateRequestDto requestDto);

    @AfterMapping
    default void setRolesIds(
            @MappingTarget UserAdminResponseDto responseDto,
            User user) {
        Set<Long> rolesIds = user.getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        responseDto.setRolesIds(rolesIds);
    }
}
