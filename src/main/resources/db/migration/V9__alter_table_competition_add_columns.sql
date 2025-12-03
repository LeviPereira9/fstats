ALTER TABLE TB_Competicao ADD COLUMN Nr_ExternoRodadaAtual BIGINT DEFAULT 1;
ALTER TABLE TB_Competicao RENAME COLUMN Nr_AtualRodada TO Nr_RodadaAtual;