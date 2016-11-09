package Database.ReflectStuff;

import java.lang.annotation.Annotation;

/**
 * Created by root on 16-11-8.
 */
public interface AnnotationCollector {
    <T extends Annotation> boolean collect(T annotation);
}