CREATE TABLE IF NOT EXISTS TB_MediasTime(
    ID_MediasTime                   BIGINT           AUTO_INCREMENT,

    ID_Competicao                   BIGINT           NOT NULL,
    ID_Time                         BIGINT           NOT NULL,

    Vl_MediaGolsProCasa             DECIMAL(7,4)     NOT NULL,
    Vl_MediaGolsContraCasa          DECIMAL(7,4)     NOT NULL,
    Vl_MediaGolsProVisitante        DECIMAL(7,4)     NOT NULL,
    Vl_MediaGolsContraVisitante     DECIMAL(7,4)     NOT NULL,

    PRIMARY KEY (ID_MediasTime),
    FOREIGN KEY (ID_Competicao)
        REFERENCES TB_Competicao(ID_Competicao),
    FOREIGN KEY (ID_Time)
        REFERENCES TB_Time(ID_Time)
)engine=InnoDB default charset=utf8mb4;