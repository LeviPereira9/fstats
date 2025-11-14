CREATE TABLE IF NOT EXISTS TB_Partida(
    ID_Partida              BIGINT AUTO_INCREMENT,
    ID_Temporada            VARCHAR(5) NOT NULL, --
    ID_Rodada               INT NOT NULL,
    ID_TimeMandante         BIGINT NOT NULL,
    Qt_TimeMandanteGol      INT NOT NULL,
    ID_TimeVisitante        BIGINT NOT NULL,
    Qt_TimeVisitanteGol     INT NOT NULL,

    PRIMARY KEY (ID_Partida),
    CONSTRAINT FK_Partida_TM FOREIGN KEY (ID_TimeMandante)
        REFERENCES TB_Time(ID_Time),
    CONSTRAINT FK_Partida_TV FOREIGN KEY (ID_TimeVisitante)
        REFERENCES TB_Time(ID_Time)
)engine=InnoDB default charset=utf8mb4;