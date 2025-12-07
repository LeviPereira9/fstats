ALTER TABLE TB_Probabilidade ADD COLUMN Nr_Rodada INTEGER NOT NULL;
ALTER TABLE TB_Probabilidade ADD COLUMN ID_Competicao BIGINT NOT NULL;
ALTER TABLE TB_Probabilidade ADD CONSTRAINT
    FOREIGN KEY (ID_Competicao)
        REFERENCES TB_Competicao(ID_Competicao);

