package br.com.larcash.config.validation.anotacao;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de validação composta que garante que um ID do tipo Integer/Long
 * não seja nulo (@NotNull) e seja um valor estritamente positivo (@Positive).
 */
@NotNull(message = "O ID não pode ser nulo.")
@Positive(message = "O ID deve ser um número positivo maior que zero.")
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface IdValido {

    String message() default "O id {nomeDoAtributo} é obrigatório e deve não deve ser negativo.";
    
    String nomeDoAtributo() default "ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}