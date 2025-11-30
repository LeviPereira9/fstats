CREATE TABLE IF NOT EXISTS TB_Competicao(
    ID_Competicao           BIGINT          AUTO_INCREMENT,
    ID_ExternoCompeticao    BIGINT          NOT NULL,

    Nm_Competicao           VARCHAR(255)    NOT NULL,
    Ds_Codigo               VARCHAR(255)    NOT NULL,
    Ds_Tipo                 VARCHAR(255)    NOT NULL,
    Ur_Emblema              VARCHAR(255)    NOT NULL,

    Qt_Partidas             INTEGER         NOT NULL,
    Dt_Inicio               DATE            NOT NULL,
    Dt_Fim                  DATE            NOT NULL,

    St_Ativo                BOOLEAN         DEFAULT TRUE,

    PRIMARY KEY (ID_Competicao),
    UNIQUE (ID_ExternoCompeticao)
)engine=InnoDB default charset=utf8mb4;