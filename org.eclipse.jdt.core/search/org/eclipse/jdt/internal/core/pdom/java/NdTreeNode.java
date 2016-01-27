/*******************************************************************************
 * Copyright (c) 2015 Google, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Stefan Xenos (Google) - Initial implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.pdom.java;

import org.eclipse.jdt.internal.core.pdom.Nd;
import org.eclipse.jdt.internal.core.pdom.NdNode;
import org.eclipse.jdt.internal.core.pdom.db.IndexException;
import org.eclipse.jdt.internal.core.pdom.field.FieldOneToMany;
import org.eclipse.jdt.internal.core.pdom.field.FieldManyToOne;
import org.eclipse.jdt.internal.core.pdom.field.StructDef;

/**
 * PDOMTreeNode elements form a tree of nodes rooted at a
 * {@link NdResourceFile}. Each node contains a list of children
 * which it declares and has a pointer to the most specific node which
 * declares it.
 * @since 3.12
 */
public abstract class NdTreeNode extends NdNode {
	public static final FieldManyToOne<NdTreeNode> PARENT;
	public static final FieldOneToMany<NdTreeNode> CHILDREN;

	public static final StructDef<NdTreeNode> type; 

	static {
		type = StructDef.create(NdTreeNode.class, NdNode.type);
		PARENT = FieldManyToOne.create(type, null);
		CHILDREN = FieldOneToMany.create(type, PARENT, 16);
		type.done();
	}

	public NdTreeNode(Nd dom, long address) {
		super(dom, address);
	}

	protected NdTreeNode(Nd pdom, NdTreeNode parent) {
		super(pdom);

		PARENT.put(pdom, this.address, parent == null ? 0 : parent.address);
	}

	/**
	 * Returns the closest ancestor of the given type, or null if none. Note that
	 * this looks for an exact match. It will not return subtypes of the given type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends NdTreeNode> T getAncestorOfType(Class<T> type) {
		long targetType = getPDOM().getNodeType(type);

		Nd pdom = getPDOM();
		long current = PARENT.getAddress(pdom, this.address);

		while (current != 0) {
			short currentType = NODE_TYPE.get(pdom, current);

			if (currentType == targetType) {
				NdNode result = load(pdom, current);

				if (type.isInstance(result)) {
					return (T) result;
				} else {
					throw new IndexException("The node at address " + current + 
							" should have been an instance of " + type.getName() + 
							" but was an instance of " + result.getClass().getName());
				}
			}

			current = PARENT.getAddress(pdom, current);
		}

		return null;
	}

	NdTreeNode getParentNode() {
		return PARENT.get(getPDOM(), this.address);
	}

	public NdBinding getParentBinding() throws IndexException {
		NdNode parent= getParentNode();
		if (parent instanceof NdBinding) {
			return (NdBinding) parent;
		}
		return null;
	}
}
