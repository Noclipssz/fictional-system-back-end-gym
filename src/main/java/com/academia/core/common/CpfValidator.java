package com.academia.core.common;

public class CpfValidator {

    /**
     * Valida se um CPF é válido usando o algoritmo oficial brasileiro.
     *
     * @param cpf CPF a ser validado (pode conter pontos e traço)
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // CPF deve ter 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (CPFs inválidos conhecidos)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Calcula o primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito > 9) {
                primeiroDigito = 0;
            }

            // Verifica o primeiro dígito
            if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
                return false;
            }

            // Calcula o segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito > 9) {
                segundoDigito = 0;
            }

            // Verifica o segundo dígito
            return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove formatação do CPF (pontos e traço).
     *
     * @param cpf CPF formatado
     * @return CPF apenas com números
     */
    public static String sanitize(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }
}
