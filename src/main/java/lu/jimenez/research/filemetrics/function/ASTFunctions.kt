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
package lu.jimenez.research.filemetrics.function

import ast.declarations.IdentifierDecl
import ast.expressions.AssignmentExpr
import ast.expressions.Expression
import ast.functionDef.FunctionDef
import ast.statements.CompoundStatement
import ast.statements.ExpressionStatement
import ast.statements.IdentifierDeclStatement
import cfg.C.CCFGFactory
import cfg.nodes.ASTNodeContainer
import lu.jimenez.research.filemetrics.node.NodeFunctions
import java.util.*

/**
 * Object containing useful function handling the AST of a function
 */
object ASTFunctions {

    /**
     * Function that return the list of parameters(arguments) of the given function and how they are passed by
     * true->reference
     *false -> value
     *
     * used by: MetricsFunction in the fan out to create a list of interesting metrics, the pass by reference/value help to detect if the parameter is of interest
     *
     * @return map of parameters (key) and if they are passed by reference or value (value)
     */
    fun listOfParameters(node: FunctionDef): Map<String, Boolean> {
        val map = HashMap<String, Boolean>()
        for (parameter in node.parameterList.parameters) {
            val isReference = parameter.type.escapedCodeStr.contains("*") || parameter.type.escapedCodeStr.contains("&")
            val name = parameter.name.escapedCodeStr
            map[name] = isReference
        }
        return map
    }

    /**
     * Function that return the list of calls made by a function
     *
     * used by: GlobalASTFunctions map of function calls
     *
     * @return list of function called //Not unique
     */
    fun listOfCallsOfAFunction(node: FunctionDef): List<String> {
        return listOfCallsOfAFunction(node.content)
    }

    /**
     * Function that return the list of calls present in a CompoundStatement
     *
     * used by: [listOfCallsOfAFunction]
     *
     * @return list of function called //Not unique
     */
    fun listOfCallsOfAFunction(content: CompoundStatement?): ArrayList<String> {
        val cfg = CCFGFactory.convert(content)
        val listofCall = ArrayList<String>()
        for (vertice in cfg.vertices) {
            if (vertice is ASTNodeContainer)
                listofCall.addAll(NodeFunctions.listofCallInASTNode(vertice.astNode))
        }
        return listofCall
    }

    /**
     * Function that return the list of all variable that have an assignement in a Function
     * This function relies on the creation of a CFG for better read of the AST
     *
     * Used by: MetricsFunction in the fan out to see all expression used (no declaration)
     *
     * @return list of Expression in [node]
     */
    fun listOfExpressionOfAFunction(node: FunctionDef): List<String> {
        return listOfExpressionOfAFunction(node.content)
    }

    /**
     * Function that return the list of all variable that have an assignement in Compound Statement
     * This function relies on the creation of a CFG for better read of the AST
     *
     * Used by: [listOfExpressionOfAFunction]
     *
     * @return list of Expression in [content]
     */
    fun listOfExpressionOfAFunction(content: CompoundStatement?): ArrayList<String> {
        val listofExpressionName = ArrayList<String>()
        val cfg = CCFGFactory.convert(content)
        for (vertice in cfg.vertices) {
            if (vertice is ASTNodeContainer) {
                val astnode = vertice.astNode
                if (astnode is ExpressionStatement && astnode.children !=null) {
                    for (identifier in astnode.children) {
                        if (identifier is AssignmentExpr) {
                            val ident: Expression = identifier.left
                            listofExpressionName.add(ident.escapedCodeStr)
                            if (identifier.right is AssignmentExpr) {
                                val otherExpression = NodeFunctions.assignementOnTheRight(identifier.right)
                                listofExpressionName.addAll(otherExpression)
                            }
                        }
                    }
                }
            }
        }
        return listofExpressionName
    }


    /**
     * Function that return the list of variable declared in a function and precise if its a pointer or not
     *
     * Used by: Metrics Function to detect the inner variable of a function and not take them into account for the fan in
     *
     * @return map of variable (key) and if they are a pointer (value)
     */
    fun listOfIdentifierDeclarationInAFunction(node: FunctionDef): Map<String, Boolean> {
        return listOfIdentifierDeclarationInAFunction(node.content)
    }

    /**
     * Function that return the list of variable declared in compound statement and precise if its a pointer or not
     *
     * Used by: [listOfIdentifierDeclarationInAFunction]
     *
     * @return map of variable (key) and if they are a pointer (value)
     */
    fun listOfIdentifierDeclarationInAFunction(content: CompoundStatement?): HashMap<String, Boolean> {
        val listofVariableName = HashMap<String, Boolean>()
        val cfg = CCFGFactory.convert(content)
        for (vertice in cfg.vertices) {
            if (vertice is ASTNodeContainer) {
                val astnode = vertice.astNode
                if (astnode is IdentifierDeclStatement) {
                    for (identifier in astnode.identifierDeclList) {
                        if (identifier is IdentifierDecl) {
                            val pointer = identifier.type.completeType?.contains("*") ?: false
                            val name: String = identifier.name.escapedCodeStr
                            listofVariableName.put(name, pointer)
                        }
                    }
                }
            }
        }
        return listofVariableName
    }

    /**
     * Function that return a list of variable declared in a function that are related to some variable of interest
     * i.e. : variable that are pointer and assign to variable of interest
     *
     * Used by: Metrics Function in the fan out method to track change on outside variable
     *
     * @return list of interesting identifier
     */
    fun listOfInterestingIdentifierDeclarationInAFunction(node: FunctionDef, listofInterestingParameters: List<String>): List<String> {
        return listOfInterestingIdentifierDeclarationInAFunction(node.content, listofInterestingParameters)
    }

    /**
     * Function that return a list of variable declared in a compound statement that are related to some variable of interest
     * i.e. : variable that are pointer and assign to variable of interest
     *
     * Used by: [listOfInterestingIdentifierDeclarationInAFunction]
     *
     * @return list of interesting identifier
     */
    fun listOfInterestingIdentifierDeclarationInAFunction(content: CompoundStatement?, listofInterestingParameters: List<String>): ArrayList<String> {
        val listofVariableName = ArrayList<String>()
        val cfg = CCFGFactory.convert(content)
        for (vertice in cfg.vertices) {
            if (vertice is ASTNodeContainer) {
                val astnode = vertice.astNode
                if (astnode is IdentifierDeclStatement) {
                    for (identifier in astnode.identifierDeclList) {
                        if (identifier is IdentifierDecl) {
                            val pointer = identifier.type.completeType?.contains("*") ?: false || identifier.type.completeType?.contains("&") ?: false
                            val name: String = identifier.name.escapedCodeStr
                            for (child in identifier.children)
                                if (child is AssignmentExpr)
                                    if (NodeFunctions.isAnyInteringTermPresent(child, listofInterestingParameters) && pointer)
                                        listofVariableName.add(name)
                        }
                    }
                }
            }
        }
        return listofVariableName
    }


    /**
     *Function that list all identifier in a Function, in case of a pointer, it only keeps the root part
     *
     * Used by : MetricsFunctions in the fan in method to find all possible use of outside variable
     *
     * @return list of main identifier
     */
    fun listOfMainIdentifierOfFunction(node: FunctionDef): List<String> {
        return listOfMainIdentifierOfFunction(node.content)
    }

    /**
     *Function that list all identifier in a Compound Statement, in case of a pointer, it only keeps the root part
     *
     * Used by : [listOfMainIdentifierOfFunction]
     *
     * @return list of main identifier
     */
    fun listOfMainIdentifierOfFunction(content: CompoundStatement?): ArrayList<String> {
        val listofIdentifier = ArrayList<String>()
        val cfg = CCFGFactory.convert(content)
        for (vertice in cfg.vertices) {
            if (vertice is ASTNodeContainer) {
                val astnode = vertice.astNode
                listofIdentifier.addAll(NodeFunctions.listofMainIdentifierofANode(astnode))
            }
        }
        return listofIdentifier
    }


}