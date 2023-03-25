package com.tafakkoor.e_learn.domain;

import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class AuthUser extends Auditable {
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @ToString.Exclude
    private Image image;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @ManyToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_roles",
            joinColumns = @JoinColumn(name = "auth_user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "auth_role_id", referencedColumnName = "id")
    )
    private Collection<AuthRole> authRoles;
    @Builder.Default
    private Status status = Status.INACTIVE;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Levels level = Levels.DEFAULT;
    private LocalDate birthDate;
    private LocalDateTime lastLogin;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer score;
    private boolean isOAuthUser;

}
