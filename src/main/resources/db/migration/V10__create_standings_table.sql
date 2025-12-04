CREATE TABLE IF NOT EXISTS TB_Classificacao(
    ID_Classificacao            BIGINT          AUTO_INCREMENT,
    Ds_Tipo                     VARCHAR(5)      NOT NULL,

    ID_Competicao               BIGINT          NOT NULL,
    ID_Time                     BIGINT          NOT NULL,

    Nr_Posicao                  INTEGER         NOT NULL,
    Qt_PartidasJogadas          INTEGER         NOT NULL,
    Ds_FormaAtual               VARCHAR(20)     NOT NULL,
    Qt_Vitoria                  INTEGER         NOT NULL,
    Qt_Empate                   INTEGER         NOT NULL,
    Qt_Derrota                  INTEGER         NOT NULL,
    Qt_Pontos                   INTEGER         NOT NULL,
    Qt_GolsPro                  INTEGER         NOT NULL,
    Qt_GolsContra               INTEGER         NOT NULL,
    Qt_SaldoGols                INTEGER         NOT NULL,

    PRIMARY KEY (ID_Classificacao),
    FOREIGN KEY (ID_Competicao)
        REFERENCES TB_Competicao(ID_Competicao)
)engine=InnoDB default charset=utf8mb4;