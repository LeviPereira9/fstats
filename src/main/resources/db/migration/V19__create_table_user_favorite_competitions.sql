CREATE TABLE TB_CompeticoesFavoritas (
    ID_Favorito             INTEGER     AUTO_INCREMENT,
    ID_Competicao           INTEGER      NOT NULL,
    ID_Usuario              BIGINT      NOT NULL,

    PRIMARY KEY (ID_Favorito),
    FOREIGN KEY (ID_Competicao)
        REFERENCES TB_Codigo(ID_Codigo),
    FOREIGN KEY (ID_Usuario)
        REFERENCES TB_Usuario(ID_Usuario),
    UNIQUE (ID_Competicao, ID_Usuario)
)engine=InnoDB default charset=utf8mb4;