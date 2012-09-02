package org.jadira.bindings.core.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jadira.bindings.core.annotation.BindingScope;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@BindingScope
public @interface Narrow {
}
