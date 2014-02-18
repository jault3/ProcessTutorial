package edu.msoe.tutorial.process.testing;

/**
 * Provides messages returned on constraint violations by the hibernate validator.
 */
public class HibernateMessage {

    /**
     * Message provided by the {@link javax.validation.constraints.Min @Min} annotation.
     */
    public static final String MIN = "must be greater than or equal to 0";
    /**
     * Message provided by the {@link org.hibernate.validator.constraints.Email @Email} annotation.
     */
    public static final String EMAIL = "not a well-formed email address";
    /**
     * Message provided by the {@link org.hibernate.validator.constraints.NotBlank @NotBlank}
     * annotation.
     */
    public static final String NOT_BLANK = "may not be empty";

    public static final String NOT_NULL = "may not be null";
}
