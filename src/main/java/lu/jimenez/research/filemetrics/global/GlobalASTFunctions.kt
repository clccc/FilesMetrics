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
package lu.jimenez.research.filemetrics.global

import ast.ASTNode
import ast.declarations.IdentifierDecl
import ast.functionDef.FunctionDef
import ast.statements.IdentifierDeclStatement
import lu.jimenez.research.filemetrics.function.ASTFunctions
import java.util.*

/**
 * Object gathering all function that are operating on an AST Node
 */
object GlobalASTFunctions {


    /**
     * Function that return the names of all function present in a list of nodes
     *
     * Used by: Code Metrics to retrieve the list of function present in a file
     *
     * @return list of all function name present in the [list of node][listnode]
     */
    fun listofFunctionName(listnode: List<ASTNode>): List<String> {
        val listofFunctionName = ArrayList<String>()
        for (node in listnode) {
            if (node is FunctionDef) {
                listofFunctionName.add(node.name.escapedCodeStr)
            }
        }
        return listofFunctionName
    }

    /**
     * Function that return the list of variable declared in a list of node
     *
     * Used by: CodeMetrics to find retrieve the list of Global Variables
     *
     * @return list of all variable declared
     */
    fun listofGlobalVariable(listnode: List<ASTNode>): List<String> {
        val listofVariableName = ArrayList<String>()
        for (node in listnode) {
            if (node is IdentifierDeclStatement) {
                for (identifier in node.identifierDeclList) {
                    if (identifier is IdentifierDecl) {
                        listofVariableName.add(identifier.name.escapedCodeStr)
                    }
                }
            }
        }
        return listofVariableName
    }

    /**
     *Function that return the list of all calls made by each functions
     *
     * Used by: FANIN & FANOUT in Code Metrics
     *
     * @return map of function name (key) and its calls (value)
     */
    @JvmStatic
    fun mapOfCallMadeByFunctions(listnode: List<ASTNode>): Map<String, List<String>> {
        val map = HashMap<String, List<String>>()
        for (node in listnode) {
            if (node is FunctionDef) {
                map[node.name.escapedCodeStr] = ASTFunctions.listOfCallsOfAFunction(node)

            }
        }
        return map
    }


}