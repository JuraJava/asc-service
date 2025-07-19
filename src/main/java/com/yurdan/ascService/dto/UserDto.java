package com.yurdan.ascService.dto;

import com.yurdan.ascService.model.enums.RoleOfEmployee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Предназначен для представления информации о пользователе в приложении  при входе в систему.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String fullName;
//    private List<String> roles;
    private RoleOfEmployee roleOfEmployee;
}
