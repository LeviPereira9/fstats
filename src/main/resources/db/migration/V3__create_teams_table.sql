CREATE TABLE IF NOT EXISTS TB_Time(
    ID_Time             BIGINT          AUTO_INCREMENT,
    ID_ExternoTime      BIGINT          NOT NULL,
    Nm_Time             VARCHAR(255)    NOT NULL,
    Nm_TimeAbreviado    VARCHAR(255)    NOT NULL,
    Cd_SiglaTime        VARCHAR(255)    NOT NULL,
    Ur_Emblema          VARCHAR(255),

    St_Ativo            BOOLEAN         DEFAULT TRUE,

    PRIMARY KEY (ID_Time),
    UNIQUE (ID_ExternoTime)
)engine=InnoDB default charset=utf8mb4;