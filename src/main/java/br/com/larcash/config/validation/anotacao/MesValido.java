package br.com.larcash.config.validation.anotacao;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de validação composta para meses (1 a 12).
 * Garante que o valor numérico informado não seja nulo e esteja compreendido no intervalo de 1 a 12.
 */
@NotNull(message = "O campo {nomeDoAtributo} não pode ser nulo.")
@Min(value = 1, message = "O campo {nomeDoAtributo} deve ser um mês válido entre 1 e 12.")
@Max(value = 12, message = "O campo {nomeDoAtributo} deve ser um mês válido entre 1 e 12.")
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface MesValido {

    String message() default "O campo {nomeDoAtributo} deve ser um mês válido entre 1 e 12.";

    /**
     * Define o nome do atributo/campo que está sendo validado.
     * Valor padrão: "Mês"
     */
    String nomeDoAtributo() default "Mês";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
