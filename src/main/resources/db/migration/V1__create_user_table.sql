CREATE TABLE IF NOT EXISTS TB_Usuario(
    ID_Usuario              BIGINT          AUTO_INCREMENT,
    Ur_Perfil               VARCHAR(255),
    Nm_Usuario              VARCHAR(255)    NOT NULL UNIQUE,
    Ds_Email                VARCHAR(255)    NOT NULL UNIQUE,
    Hs_Senha                VARCHAR(255)    NOT NULL,
    Dt_Nascimento           DATE            NOT NULL,
    Dt_Criacao              DATE            NOT NULL,
    Dt_Atualizacao          DATETIME,
    ID_UsuarioAtualizador   BIGINT,
    Fl_Verificado           BOOLEAN         DEFAULT FALSE,
    Fl_Excluido             BOOLEAN         DEFAULT FALSE,
    En_Cargo                ENUM('USER', 'MOD', 'ADMIN', 'SUPER_ADMIN'),
    Tk_VersaoJWT            VARCHAR(16),

    PRIMARY KEY (ID_Usuario)
)engine=InnoDB default charset=utf8mb4;