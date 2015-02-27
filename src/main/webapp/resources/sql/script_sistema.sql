ALTER TABLE public.parametrorepasse ADD COLUMN codbanco varchar(10) DEFAULT '001';
ALTER TABLE public.parametrorepasse ADD COLUMN descricaobanco varchar(50) DEFAULT 'Banco do Brasil';

--REMOÇÃO DA RESTRIÇÃO DE UNICIDADE DA MATRICULA DO RESPONSÁVEL E ADIÇÃO DE UMA RESTRIÇÃO DE UNICIDADE PARA OS MATRICULA E ATIVO DO RESPONSÁVEL
ALTER TABLE public.responsavel DROP CONSTRAINT responsavel_matriculafuncional_key;
ALTER TABLE public.responsavel ADD CONSTRAINT responsavel_matriculafuncional_ativo_key UNIQUE (matriculafuncional,ativo);

--REMOÇÃO DA RESTRIÇÃO DE UNICIDADE DO USERNAME DE USUÁRIO
ALTER TABLE public.user DROP CONSTRAINT user_username_key;
ALTER TABLE public.user ADD CONSTRAINT user_username_ativo_key UNIQUE (username,ativo);

--REMOÇÃO DA RESTRIÇÃO DE UNICIDADE DO USERNAME DE USUÁRIO
ALTER TABLE public.unidade DROP CONSTRAINT unidade_nome_key;
ALTER TABLE public.unidade ADD CONSTRAINT unidade_nome_ativo_key UNIQUE (nome,ativo);
