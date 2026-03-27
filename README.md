# 🚘💸 SmartCar - Uma Solução inteligente para Precificação de Veículos

###  Projeto desenvolvido para a matéria de Projeto Integrador I

## 📋 Requisitos Funcionais
> Lista de funcionalidades obrigatórias do sistema
### 🔐 Módulo 1 — Autenticação
- RF-01: O sistema deve permitir que o usuário se cadastre com nome, e-mail e senha. </br>
*A senha deve ser armazenada com hash e o e-mail deve ser único.*
- RF-02: O sistema deve autenticar o usuário via e-mail e senha, retornando um token JWT. </br>
*O token deve ser enviado no header das requisições protegidas.*
- RF-03: O sistema deve rejeitar requisições sem token ou com token inválido/expirado. </br>
*Deve retornar HTTP 401 e redirecionar o usuário para login.* 
### 🚗 Módulo 2 — Cadastro de Veículo
- RF-04: O sistema deve consultar a API FIPE e retornar as marcas disponíveis. </br>
*Exibido em um seletor no formulário.*
- RF-05: O sistema deve carregar os modelos com base na marca selecionada. </br>
*A listagem deve ser dinâmica.*
- RF-06: O sistema deve carregar os anos disponíveis com base no modelo selecionado. </br>
*Cada ano representa uma versão FIPE.*
- RF-07: O usuário deve informar quilometragem e estado de conservação. </br>
*Opções: Ótimo, Bom, Regular.*
- RF-08: O usuário deve informar o preço desejado do veículo. </br>
*Campo obrigatório.*
- RF-09: O sistema deve associar o veículo ao usuário autenticado. </br>
*Um usuário pode ter vários veículos.*
### 📊 Módulo 3 — Avaliação de Preço
- RF-10: O sistema deve consultar o valor FIPE com base em marca, modelo e ano.
- RF-11: O sistema deve calcular um preço ajustado com base na quilometragem e conservação. </br>
*Lógica aplicada no backend.*
- RF-12: O sistema deve classificar o preço informado pelo usuário. </br>
  Classificações:
  - Ótimo negócio (até -5%)
  - Na média (±5%)
  - Acima da média (+5% a +15%)
  - Difícil de vender (> +15%)
- RF-13: O sistema deve retornar: </br>
  - Valor FIPE
  - Preço ajustado
  - Classificação
  - Dica de venda
- RF-14: O sistema deve salvar a avaliação vinculada ao usuário e ao veículo. </br>
*Cada avaliação gera um novo registro.*
### 📜 Módulo 4 — Histórico de Avaliações
- RF-15: O sistema deve listar todas as avaliações do usuário. </br>
*Exibindo veículo, preço, classificação e data.*
- RF-16: O usuário deve visualizar detalhes de uma avaliação. </br>
*Incluindo FIPE, ajustado, classificação e dica.*
- RF-17: O usuário deve poder reavaliar um veículo. </br>
*Gerando um novo registro sem alterar o original.*

## 🏗️ Modelagem do Sistema (UML)
### Abaixo está o diagrama de classes que representa a estrutura do banco de dados e as relações entre as entidades:
![Diagrama UML do SmartCar](assets/SmartCarUML.png)
