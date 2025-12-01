ALTER TABLE TB_Competicao ADD COLUMN
    Nr_AtualRodada  INTEGER     NOT NULL;

ALTER TABLE TB_Competicao ADD COLUMN
    St_Competicao  VARCHAR(100) DEFAULT 'Em andamento' NOT NULL