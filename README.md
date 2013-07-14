abstract-final-j
================

Annotation library to enable a class to enforce that a method 
can only be overridden one time in any subtype hierarchy, Just 
mark any method @AbstractFinal. 

It works on abstract or concrete methods of any class or interface.

Release Notes : 

v1.1 : 

    Removed @SupportedSourceVersion from processor as it needlessly
limits using the library in Java 6 environments. This annotation is
documented as specifying the "latest" version of Java that would be
supported by the annotation. Due to the enum that is used not 
having entries for future versions of Java, the effect is that the
specified version is actually the "only" version that is supported.

ie: @SupportedSourceVersion(SourceVersion.RELEASE_7) really means only
Java 7 can use this annotation. 


