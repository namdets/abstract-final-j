/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Jason Stedman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jasonstedman.extensions;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

/** 
 * This annotation processor examines methods marked {@link com.jasonstedman.extensions.AbstractFinal}
 * 
 * @author Jason Stedman
 * @version 1.1
 * 
 */
@SupportedAnnotationTypes("com.jasonstedman.extensions.AbstractFinal")
public class AbstractFinalAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> elements,
			RoundEnvironment roundEnv) {
		Set<? extends Element> rootElements = roundEnv.getRootElements();
		for (Element element : rootElements)
		{	
			TypeElement classElement = (TypeElement) element;
			for(Element subElement : element.getEnclosedElements())
				if(subElement.getKind()==ElementKind.METHOD){
					ExecutableElement method = (ExecutableElement)subElement;
					for(TypeMirror intrface : classElement.getInterfaces()){
						if(isMethodOverriddenAndAbstractFinal(method, intrface)){
							if(!method.getModifiers().contains(Modifier.FINAL)){
								error(method,"Method "+ method.toString() +" overrides the same method marked @AbstractFinal declared in interface " + intrface.toString() + " but is not marked final.");
							}											
						}
					}
					testSuperClasses(classElement, method);
				}
		}
		return false;
	}

	private void testSuperClasses(TypeElement element, ExecutableElement method) {
		TypeMirror superType = element.getSuperclass();
		if((!superType.toString().equals("<none>")) && isMethodOverriddenAndAbstractFinal(method, superType)){
			if(!method.getModifiers().contains(Modifier.FINAL)){
				error(method,"Method "+ method.toString() +" overrides the same method marked @AbstractFinal declared in superclass " + superType.toString() + " but is not marked final.");
			}
		}
	}

	private boolean isMethodOverriddenAndAbstractFinal(ExecutableElement method,
			TypeMirror superType) {
		TypeElement e = (TypeElement) processingEnv.getTypeUtils().asElement(superType);
		for(Element element :e.getEnclosedElements()){
			if(element.getKind()==ElementKind.METHOD){
				if(element.getAnnotation(AbstractFinal.class)!=null){
					if(element.getSimpleName().equals(method.getSimpleName())){
						return true;
					}
				}
			}
		}
		testSuperClasses(e, method);
		return false;
	}

	private void error(Element element, String message) {
		Messager messager = processingEnv.getMessager();
		messager.printMessage(
				Kind.ERROR, 
				message,
				element);
	}

}
