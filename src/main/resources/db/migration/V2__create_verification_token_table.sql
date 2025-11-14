CREATE TABLE IF NOT EXISTS TB_TokenVerificacao(
    ID_TokenVerificacao     BIGINT              AUTO_INCREMENT,
    ID_Usuario              BIGINT              NOT NULL,
    Ds_Token                VARCHAR(6)          NOT NULL,
    Dt_Criacao              DATETIME,
    Dt_Expiracao            DATETIME            NOT NULL,
    Tx_Contexto             VARCHAR(255),
    En_Tipo                 ENUM('CONFIRMATION', 'PASSWORD', 'EMAIL'),
    Ft_Usado                BOOLEAN DEFAULT     FALSE,

    PRIMARY KEY (ID_TokenVerificacao),
    CONSTRAINT FK_Usuario_TV FOREIGN KEY (ID_Usuario)
        REFERENCES TB_Usuario(ID_Usuario) ON DELETE CASCADE,
    UNIQUE UN_Usuario_TV (ID_Usuario, Ds_Token)
)engine=InnoDB default charset=utf8mb4;