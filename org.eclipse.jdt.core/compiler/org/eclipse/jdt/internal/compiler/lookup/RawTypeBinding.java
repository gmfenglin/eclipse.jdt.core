/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

/**
 * Denote a raw type, i.e. a generic type referenced without any type arguments.
 * e.g. X<T extends Exception> can be used a raw type 'X', in which case it
 * 	will behave as X<Exception>
 */
public class RawTypeBinding extends ParameterizedTypeBinding {
    
	public RawTypeBinding(ReferenceBinding type, LookupEnvironment environment){
		super(type, parameterErasures(type), environment);
		this.modifiers ^= AccGenericSignature;
	}    
	/**
	 * Ltype<param1 ... paremN>;
	 * LY<TT;>;
	 */
	public char[] genericTypeSignature() {
	    if (this.genericTypeSignature != null) return this.genericTypeSignature;
		return this.genericTypeSignature = this.type.genericTypeSignature();
	}		
	/**
     * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#isCompatibleWith(org.eclipse.jdt.internal.compiler.lookup.TypeBinding)
     */
    public boolean isCompatibleWith(TypeBinding right) {
      	if (this.type.isCompatibleWith(right)) return true;
      	if (right.isParameterizedType()) {
      		ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding) right;
      		return isCompatibleWith(parameterizedType.type);
      	}
      	return false;
    }
	
	/**
	 * Raw type is not treated as a standard parameterized type
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#isParameterizedType()
	 */
	public boolean isParameterizedType() {
	    return false;
	}	
	public boolean isRawType() {
	    return true;
	}	
	private static TypeBinding[] parameterErasures(ReferenceBinding type) {
		TypeVariableBinding[] typeVariables = type.typeVariables();
		int length = typeVariables.length;
		TypeBinding[] typeArguments = new TypeBinding[length];
		for (int i = 0; i < length; i++) {
		    typeArguments[i] = typeVariables[i].erasure();
		}
		return typeArguments;
	}
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#readableName()
	 */
	public char[] readableName() /*java.lang.Object,  p.X<T> */ {
	    char[] readableName;
		if (isMemberType()) {
			readableName = CharOperation.concat(this.type.enclosingType().readableName(), sourceName, '.');
		} else {
			readableName = CharOperation.concatWith(this.type.compoundName, '.');
		}
		return readableName;
	}
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#shortReadableName()
	 */
public char[] shortReadableName() /*Object*/ {
    char[] shortReadableName;
	if (isMemberType()) {
		shortReadableName = CharOperation.concat(this.type.enclosingType().shortReadableName(), sourceName, '.');
	} else {
		shortReadableName = this.type.sourceName;
	}
	return shortReadableName;
}
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#superclass()
	 */
	public ReferenceBinding superclass() {
	    return this.type.superclass();
	}	
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#superInterfaces()
	 */
	public ReferenceBinding[] superInterfaces() {
		return this.type.superInterfaces();
	}	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(10);
		buffer.append(this.type);
		return buffer.toString();
	}	
}
