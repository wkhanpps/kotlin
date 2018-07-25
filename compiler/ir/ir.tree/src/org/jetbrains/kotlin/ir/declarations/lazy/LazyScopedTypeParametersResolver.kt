/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations.lazy

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.util.*
import java.util.*

class LazyScopedTypeParametersResolver(private val symbolTable: SymbolTable) : TypeParametersResolver {

    private val typeParameterScopes = ArrayDeque<IrTypeParametersContainer>()

    override fun enterTypeParameterScope(typeParametersContainer: IrTypeParametersContainer) {
        typeParameterScopes.addFirst(
            typeParametersContainer
        )
    }

    override fun leaveTypeParameterScope() {
        typeParameterScopes.removeFirst()
    }

    //TODO optimize
    override fun resolveScopedTypeParameter(typeParameterDescriptor: TypeParameterDescriptor): IrTypeParameterSymbol? {
        return symbolTable.referenceTypeParameter(typeParameterDescriptor)
    }

    private fun calcParent(container: IrTypeParametersContainer): IrTypeParametersContainer? {
        return when(container) {
            is IrClass -> if (container.isObject || !container.isInner) null else container.parent
            else -> (container as? IrDeclaration)?.parent
        } as? IrTypeParametersContainer
    }

}