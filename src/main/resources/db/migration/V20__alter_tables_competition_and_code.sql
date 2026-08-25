/*ADD: boolean para saber se a competição deve continuar sendo sincronizada.*/
ALTER TABLE  TB_Codigo ADD COLUMN Fl_Ativa BOOLEAN DEFAULT TRUE;

/*ADD: relação de código para competição.*/
ALTER TABLE TB_Competicao ADD COLUMN ID_Codigo INTEGER;

UPDATE TB_Competicao c
    JOIN TB_Codigo cod ON cod.Ds_Codigo = c.Ds_Codigo
    SET c.ID_Codigo = cod.ID_Codigo;

ALTER TABLE TB_Competicao
    ADD CONSTRAINT FK_Competicao_Codigo
    FOREIGN KEY (ID_Codigo)
    REFERENCES TB_Codigo(ID_Codigo);

ALTER TABLE TB_Competicao DROP COLUMN Ds_Codigo;