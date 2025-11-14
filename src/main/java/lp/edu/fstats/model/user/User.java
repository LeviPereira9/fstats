package lp.edu.fstats.model.user;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.util.AuthUtil;
import lp.edu.fstats.util.BrazilTimeUtil;
import lp.edu.fstats.util.TokenGeneratorUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "TB_Usuario")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Usuario")
    private Long id;

    @Column(name = "Ur_Perfil")
    private String profilePicture;

    @Column(name = "Nm_Usuario")
    private String username;

    @Column(name = "Ds_Email")
    private String email;

    @Column(name = "Hs_Senha")
    private String password;

    @Column(name = "Dt_Nascimento")
    private LocalDate dateOfBirth;

    @Column(name = "Dt_Criacao")
    private LocalDate createdAt = BrazilTimeUtil.nowDate();

    @Column(name = "Dt_Atualizacao")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UsuarioAtualizador")
    private User updatedBy;

    @Column(name = "Fl_Excluido")
    private boolean deleted = false;

    @Column(name = "Fl_Verificado")
    private boolean verified = false;

    @Column(name = "En_Cargo")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "Tk_VersaoJWT")
    private String tokenVersion = TokenGeneratorUtil.generateJwtVersion();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = role.getName();

        return switch (roleName) {
            case "SUPER_ADMIN" -> List.of(
                    new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_MOD"),
                    new SimpleGrantedAuthority("ROLE_USER"));
            case "ADMIN" -> List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_MOD"),
                    new SimpleGrantedAuthority("ROLE_USER"));
            case "MOD" -> List.of(
                    new SimpleGrantedAuthority("ROLE_MOD"),
                    new SimpleGrantedAuthority("ROLE_USER"));
            default -> List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        };
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public void verify(){
        verified = true;
    }

    public void update(){
        update(AuthUtil.getRequester());
    }

    public void update(User requester){
        this.updatedAt = BrazilTimeUtil.nowDateTime();
        this.updatedBy = requester;
    }

    public void rotateJwtVersion() {
        tokenVersion = TokenGeneratorUtil.generateJwtVersion();
    }

    public void changePassword(String password){
        this.password = password;

        rotateJwtVersion();
        update();
    }

    public void softDelete() {
        deleted = true;
        update();
    }
}
