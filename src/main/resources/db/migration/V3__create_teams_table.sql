CREATE TABLE IF NOT EXISTS TB_Time(
    ID_Time BIGINT AUTO_INCREMENT,
    Nm_Time VARCHAR(255) NOT NULL,
    Ur_Emblema VARCHAR(255),

    PRIMARY KEY (ID_Time)
)engine=InnoDB default charset=utf8mb4;