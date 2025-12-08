CREATE TABLE IF NOT EXISTS TB_Codigo(
    ID_Codigo INTEGER AUTO_INCREMENT,
    Ds_Codigo VARCHAR(10) NOT NULL,

    PRIMARY KEY (ID_Codigo),
    UNIQUE (Ds_Codigo)
)engine=InnoDB default charset=utf8mb4;

INSERT INTO TB_Codigo (Ds_Codigo)
    VALUES ('PL'),
           ('BSA'),
           ('PD'),
           ('BL1'),
           ('SA'),
           ('FL1');

