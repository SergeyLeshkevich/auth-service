package ru.clevertec.auth.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {

    public static String ROLE_JOURNALIST = "ROLE_JOURNALIST";

    public static String ROLE_ADMIN = "ROLE_ADMIN";

    public static String ROLE_SUBSCRIBER = "ROLE_SUBSCRIBER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
