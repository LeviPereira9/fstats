CREATE TABLE IF NOT EXISTS TB_Probabilidade(
    ID_Probabilidade        BIGINT      AUTO_INCREMENT,
    ID_Partida              BIGINT      NOT NULL,
    Vl_Probabilidade_5      NUMERIC     NOT NULL,
    Vl_Probabilidade_15     NUMERIC     NOT NULL,
    Vl_Probabilidade_25     NUMERIC     NOT NULL,

    PRIMARY KEY (ID_Probabilidade),
    CONSTRAINT FK_Partida_Probabilidade FOREIGN KEY(ID_Partida)
        REFERENCES TB_Partida(ID_Partida)
)engine=InnoDB default charset=utf8mb4;
