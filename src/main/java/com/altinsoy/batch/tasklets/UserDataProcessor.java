package com.altinsoy.batch.tasklets;

import com.altinsoy.batch.dto.UserDto;
import com.altinsoy.batch.entity.User;
import org.springframework.batch.item.ItemProcessor;

public class UserDataProcessor implements ItemProcessor<User, UserDto> {
    @Override
    public UserDto process(User item) throws Exception {
        int id = item.getId();
        if (id > 10) {
            return null;
        }
        return new UserDto(item, id);
    }
}
