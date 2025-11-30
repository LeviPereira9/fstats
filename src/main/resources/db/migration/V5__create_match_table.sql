CREATE TABLE IF NOT EXISTS TB_Partida(
    ID_Partida              BIGINT          AUTO_INCREMENT,
    ID_ExternoPartida       BIGINT          NOT NULL,
    ID_Competicao           BIGINT          NOT NULL,
    Dt_Partida              DATETIME,
    St_Partida              VARCHAR(50)     NOT NULL,
    Nr_Rodada               INTEGER,
    Ds_Estagio              VARCHAR(50),
    ID_TimeCasa             BIGINT          NOT NULL,
    ID_TimeVisitante        BIGINT          NOT NULL,
    Ds_Ganhador             VARCHAR(255),
    Qt_GolTimeCasa          INTEGER         NOT NULL,
    Qt_GolTimeVisitante     INTEGER         NOT NULL,

    St_Ativo                BOOLEAN         DEFAULT TRUE,

    PRIMARY KEY (ID_Partida),
    UNIQUE (ID_Partida),
    FOREIGN KEY (ID_TimeCasa)
        REFERENCES TB_Time(ID_Time),
    FOREIGN KEY (ID_TimeVisitante)
        REFERENCES TB_Time(ID_Time),
    FOREIGN KEY (ID_Competicao)
        REFERENCES TB_Competicao(ID_Competicao)
)engine=InnoDB default charset=utf8mb4;
