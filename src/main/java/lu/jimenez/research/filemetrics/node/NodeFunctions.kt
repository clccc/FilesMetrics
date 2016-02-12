/////////////////////////////////////////////////////////////////////////////////////////
//                 University of Luxembourg  -
//                 Interdisciplinary center for Security and Trust (SnT)
//                 Copyright © 2016 University of Luxembourg, SnT
//
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 3 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//
//
//    Author: Matthieu Jimenez – SnT – matthieu.jimenez@uni.lu
//
//////////////////////////////////////////////////////////////////////////////////////////
package lu.jimenez.research.filemetrics.node

import ast.ASTNode
import ast.declarations.ClassDefStatement
import ast.declarations.IdentifierDecl
import ast.expressions.*
import ast.functionDef.FunctionDef
import ast.statements.CompoundStatement
import ast.statements.ForStatement
import ast.statements.IdentifierDeclStatement
import cfg.C.CCFGFactory
import cfg.nodes.ASTNodeContainer
import java.util.*


/**
 * Object gathering function working at an AST Node Level
 */
object NodeFunctions {

    /**
     * Function that given [node] an ASTNode will look into all its children looking for the first identifier
     *
     * Used by: *None* was previously used to find the name of the identifier in case of a pointer, stay here in case of future use
     *
     * @return the string corresponding to this identifier or null if none were found
     */
    fun findFirstIdentifier(node: ASTNode): String? {
        if (node is Identifier)
            return node.escapedCodeStr
        if (node.children.size > 0) {
            return findFirstIdentifier(node.getChild(0))
        } else return null
    }

    /**
     * Function to count the number of variable in an AST Node
     * used by: [CodeMetrics] to compute the number of variable present in each of the main node
     *
     * Formula:
     *  * Function -> +1
     *  * IdentifierDeclStatement -> +1
     *  * Class Def -> +1 (in C class -> struct)
     *
     * @return the number of variable in [node]
     */
    fun countingVariableOfASTNode(node: ASTNode): Int {
        var numberofVariable = 0
        when (node) {
            is FunctionDef -> {
                numberofVariable++
                val cfg = CCFGFactory.convert(node.content)
                for (vertice in cfg.vertices) {
                    if (vertice is ASTNodeContainer) {
                        if (vertice.astNode is IdentifierDeclStatement)
                            numberofVariable++
                    }
                }
            }
            is IdentifierDeclStatement -> {
                numberofVariable++
            }
            is ClassDefStatement -> {
                numberofVariable++
                for (statement in node.content.statements) {
                    if (statement is ForStatement) {
                        numberofVariable++
                    }
                    if (statement is IdentifierDeclStatement) {
                        numberofVariable++
                    }
                }
            }
            is CompoundStatement -> {
                val cfg = CCFGFactory.convert(node)
                for (vertice in cfg.vertices) {
                    if (vertice is ASTNodeContainer) {
                        if (vertice.astNode is IdentifierDeclStatement)
                            numberofVariable++
                    }
                }
            }
        }
        return numberofVariable
    }

    /**
     * Function to retrieve the list of call present in an AST Node
     *
     * Used by: ASTFunction listofcalls of a function, the ASTNode here is a node of the cfg
     *
     * @return list of the name of all function called
     */
    fun listofCallInASTNode(astNode: ASTNode): List<String> {
        val listcall = ArrayList<String>()
        if (astNode is Callee)
            listcall.add(astNode.escapedCodeStr)
        if (astNode.childCount > 0) {
            for (child  in astNode.children) {
                listcall.addAll(listofCallInASTNode(child))
            }
        }
        return listcall
    }

    /**
     * Function that look at all Identifier present in an AST Node and return true if at least one math one of
     * the identifier present in the given list of interesting parameter
     *
     * Used by: AST Functions in the listofInterestingIdentifierDeclareofFunction to verify
     *
     * @return Boolean present or not
     */
    fun isAnyInteringTermPresent(node: ASTNode, listofInterestingParameters: List<String>): Boolean {
        try {
            if (node is Identifier) {
                var interest = false
                for (variable in listofInterestingParameters) {
                    if (node.escapedCodeStr.startsWith(variable)) interest = true
                }
                return interest
            } else if (node.children != null) {
                for (child in node.children) {
                    if (isAnyInteringTermPresent(child, listofInterestingParameters)) return true
                }
            }
            return false
        } catch(e: NullPointerException) {
            return false
        }
    }

    /**
     *Function that will return all identifier of a node But In case of a pointer only the first element
     *
     * Used By: AST Functions in the listofMainIdentifierOfFunction function
     *
     * @return List of all identifier
     */
    fun listofMainIdentifierofANode(astnode: ASTNode): List<String> {
        //Specific Handling of pointer
        val listOfIdentifier = ArrayList<String>()
        if (astnode is PtrMemberAccess)
            listOfIdentifier.add(astnode.children.first.escapedCodeStr)
        //Handling Identifier with assignement (avoid double add)
        else if (astnode is IdentifierDecl) {
            listOfIdentifier.addAll(listofMainIdentifierofANode(astnode.name))
            for (child in astnode.children) {
                if (child is AssignmentExpr) {
                    listOfIdentifier.addAll(listofMainIdentifierofANode(child.right))
                }
            }
        } else if (astnode is CallExpression) {
            for (child in astnode.children) {
                if (child is ArgumentList) {
                    for (argument in child.children)
                        listOfIdentifier.addAll(listofMainIdentifierofANode(argument))
                    break
                }
            }
        } else {

            if (astnode is Identifier)
                listOfIdentifier.add(astnode.escapedCodeStr)
            if (astnode.children != null) {
                for (child in astnode.children) {
                    listOfIdentifier.addAll(listofMainIdentifierofANode(child))
                }
            }

        }
        return listOfIdentifier
    }

    /**
     * Function that will gather all identifier from an Assignement Expression
     * This was created to handled chained assignement
     *
     * Used by: AST Functions in the listOfExpressionOfAFunction (need to work on Expression)
     *
     * @return list od identifier
     */
    fun assignementOnTheRight(node: Expression): List<String> {
        val listofExpressionName = ArrayList<String>()
        if (node is AssignmentExpr) {
            val ident: Expression = node.left
            listofExpressionName.add(ident.escapedCodeStr)


            listofExpressionName.addAll(assignementOnTheRight(node.right))
        }
        return listofExpressionName
    }

}