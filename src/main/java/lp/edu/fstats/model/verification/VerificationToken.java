package lp.edu.fstats.model.verification;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.util.BrazilTimeUtil;
import lp.edu.fstats.util.ExpirationUtil;
import lp.edu.fstats.util.TokenGeneratorUtil;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_TokenVerificacao")
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TokenVerificacao")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Usuario")
    private User user;

    @Column(name = "Ds_Token")
    private String token = TokenGeneratorUtil.generateVerificationToken();

    @Column(name = "Tx_Contexto")
    private String context;

    @Column(name = "Dt_Criacao")
    private LocalDateTime createdAt = BrazilTimeUtil.nowDateTime();

    @Column(name = "Dt_Expiracao")
    private LocalDateTime expiresAt = ExpirationUtil.defaultVerificationTime();

    @Column(name = "En_Tipo")
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name = "Ft_Usado")
    private boolean used = false;

    public boolean isExpired(){
        return BrazilTimeUtil.nowDateTime().isAfter(expiresAt);
    }

    public VerificationToken(){}

    public VerificationToken(User user, TokenType type){
        this.user = user;
        this.type = type;
    }
}
