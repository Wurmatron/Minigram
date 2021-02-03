package minigram.utils.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method must return EndpointData
 * Method parameters must be an instanceof IModel
 *
 * @see minigram.utils.wrapper.EndpointData
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Endpoint {

    /**
     * URL for the endpoint
     */
    String endpoint();

    /**
     * Type of request this method is requesting
     */
    RequestType type() default RequestType.GET;

}
