CREATE TABLE IF NOT EXISTS TB_TimeCompeticao(
    ID_TimeCompeticao BIGINT AUTO_INCREMENT,

    ID_Time BIGINT NOT NULL,
    ID_Competicao BIGINT NOT NULL,

    PRIMARY KEY (ID_TimeCompeticao),
    FOREIGN KEY (ID_Time)
        REFERENCES TB_Time(ID_Time),
    FOREIGN KEY (ID_Competicao)
        REFERENCES TB_Competicao(ID_Competicao),
    UNIQUE (ID_Time, ID_Competicao)
)engine=InnoDB default charset=utf8mb4;