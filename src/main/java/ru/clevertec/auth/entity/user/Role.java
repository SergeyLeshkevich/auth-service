package ru.clevertec.auth.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    public static final String ROLE_JOURNALIST = "ROLE_JOURNALIST";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String ROLE_SUBSCRIBER = "ROLE_SUBSCRIBER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
