package com.altinsoy.batch.service;

import com.altinsoy.batch.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Getter
@Setter
public class UserGenerator {

    private static final Random RANDOM = new Random();

    private static final String[] NAMES = {
            "John", "Emma", "Michael", "Sophia", "William", "Olivia", "James", "Amelia", "Benjamin", "Isabella"
            // Add more names if needed
    };

    private static final String[] SURNAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"
            // Add more surnames if needed
    };

    public static List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = NAMES[RANDOM.nextInt(NAMES.length)];
            String surname = SURNAMES[RANDOM.nextInt(SURNAMES.length)];
            users.add(new User(name, surname));
        }
        return users;
    }
}
