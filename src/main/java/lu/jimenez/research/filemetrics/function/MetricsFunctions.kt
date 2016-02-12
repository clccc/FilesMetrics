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

import ast.ASTNode
import ast.functionDef.FunctionDef
import ast.functionDef.ReturnType
import ast.statements.*
import cfg.C.CCFGFactory
import cfg.nodes.ASTNodeContainer
import org.apache.commons.lang3.StringUtils
import java.util.*


/**
 * Object gathering all method operating on a function Def node and computing metrics
 */
object MetricsFunctions {


    /**
     * Function to compute the [McCabe Cyclomatic Complexity][https://en.wikipedia.org/wiki/Cyclomatic_complexity] of a function
     *  Formula: **number of edges - number of vertices (node) + 2  (based on the Control Flow Graph)**
     *
     *  used by: CodeMetrics.cyclomaticComplexity() to compute the complexity of each function
     *
     * @return cyclomatic complexity of [the function][node]
     */
    fun cyclomaticComplexityOfFunction(node: FunctionDef): Int {
        val cfg = CCFGFactory.convert(node.content)
        return cfg.edges.size - cfg.vertices.size + 2
    }

    /**
     * Function to compute the strict Cyclomatic Complexity of a Function
     * This metric is also called CC2  or extended cyclmatic complexity and correspond to a complexity with Boolean operator
     * Formula: **Cyclomatic complexity + Boolean operators**
     *
     * used by: CodeMetrics.strictCyclomaticComplexity to compute the strict complexity of each function
     *
     * @return strict Cyclomatic Complexity of [the function][node]
     */
    fun strictCyclomaticComplexityOfFunction(node: FunctionDef): Int {
        val cfg = CCFGFactory.convert(node.content)
        var i = 0
        for (vertice in cfg.vertices) {
            if (vertice is ASTNodeContainer) {
                val vnode = vertice.astNode
                if (vnode is Condition) {
                    i += StringUtils.countMatches(vnode.escapedCodeStr, "&&")
                    i += StringUtils.countMatches(vnode.escapedCodeStr, "||")
                }
            }
        }
        return cfg.edges.size - cfg.vertices.size + i + 2
    }

    /**
     * Function to compute the modified Cyclomatic Complexity of a Function
     * This metric is also called CC3 or complexity without case
     * Formula: CC - switchNumber
     *
     * choice of implementation: count the number *N* of edge going out of a node, if N>2 increment switchnumber by N-2
     *
     * used by: CodeMetrics.modifiedCyclomaticComplexity
     *
     * @return modified Cyclomatic Complexity of [the function][node]
     */
    fun modifiedCyclomaticComplexityOfFunction(node: FunctionDef): Int {
        val cfg = CCFGFactory.convert(node.content)
        val outdegreelist = HashMap<ASTNodeContainer, Int>()
        var switchnumber = 0
        for (edge in cfg.edges) {
            val source = edge.source
            if (source is ASTNodeContainer) {
                if (outdegreelist.containsKey(source)) {
                    outdegreelist[source] = outdegreelist[source]!! + (1)
                    if (outdegreelist[source]!! > 2) switchnumber++
                } else
                    outdegreelist[source] = 1
            }
        }
        return cfg.edges.size - cfg.vertices.size - switchnumber + 2
    }

    /**
     * Function to compute the [essential Complexity][https://en.wikipedia.org/wiki/Essential_complexity_(numerical_measure_of_%22structuredness%22)] of a function
     * TODO :(Might need verification)
     * Correspond to the cyclomatic complexity of the reduced graph
     *
     * Formula: **1 + number of JumpStatement - number of conditional Statement (IF/Switch) if =2 -> =1**
     *
     * used by:  CodeMetrics.essentialComplexity
     *
     * @return the essential complexity of [the function][node]
     */
    fun essentialComplexityOfFunction(node: FunctionDef): Int {
        val cfg = CCFGFactory.convert(node.content)
        var numberofif = 0
        var numberofJump = 0
        for (edge in cfg.edges) {
            val source = edge.source
            if (source is ASTNodeContainer) {
                val astNode = source.astNode
                if (astNode is SwitchStatement || astNode is IfStatement) {
                    numberofif++
                } else if (astNode is JumpStatement) numberofJump++
            }
        }
        var calcul = 1 + numberofJump - numberofif
        if (calcul == 2) calcul = 1
        return calcul
    }

    /**
     * Function to compute the maximum nesting of a Function
     * This function is recursive and will look at all children of an AST in order to fin the maximum nesting
     * Nesting is increased by one in case of:
     *  * While
     *  * For
     *  * Do
     *  * Switch
     *  * If/Else
     *
     *  used by : CodeMetrics.maxNesting
     *
     * @return the maximum nesting of [the function][node]
     */
    fun maxNestingFunction(node: ASTNode, nesting: Int = 0): Int {
        when (node) {
            is FunctionDef -> {
                val listofNesting = ArrayList<Int>()
                listofNesting.add(0)
                for (statement in node.content.statements) {
                    listofNesting.add(maxNestingFunction(statement, 0))
                }
                return Collections.max(listofNesting)
            }
            is WhileStatement, is ForStatement, is DoStatement, is SwitchStatement -> {
                val listofNesting = ArrayList<Int>()
                listofNesting.add(0)
                for (child in node.children)
                    listofNesting.add(maxNestingFunction(child, nesting + 1))
                return Collections.max(listofNesting)

            }
            is IfStatement -> {
                val listofNesting = ArrayList<Int>()
                for (child in node.children)
                    listofNesting.add(maxNestingFunction(child, nesting + 1))
                if (node.elseNode != null) {
                    val elsenode = node.elseNode
                    for (child in elsenode.children)
                        listofNesting.add(maxNestingFunction(child, nesting + 1))
                }
                return Collections.max(listofNesting)
            }
            is CompoundStatement -> {
                val listofNesting = ArrayList<Int>()
                listofNesting.add(0)
                for (statement in node.statements) {
                    listofNesting.add(maxNestingFunction(statement, nesting))
                }
                return Collections.max(listofNesting)
            }
            else -> return nesting
        }
    }

    /**
     * Function to compute the FanIN (number of input of a function)
     *
     * @return the FANIN of [the function][node]
     */
    fun fanInOfFunction(node: FunctionDef, mapOfCalls: Map<String, List<String>>): Int {
        //Name of the function
        val nameFunction = node.name.escapedCodeStr

        var fanin = 0
        val listofCall = ArrayList<String>()
        //CallBy
        for (function in mapOfCalls) {
            if (function.key != nameFunction) {
                for (call in function.value) {
                    if (call == nameFunction) {
                        fanin++
                        break;
                    }
                }
            } else listofCall.addAll(function.value)
        }


        //list of declared variable
        val listOfDeclaredVariable = ASTFunctions.listOfIdentifierDeclarationInAFunction(node).keys
        val listOfIdentifier = HashSet(ASTFunctions.listOfMainIdentifierOfFunction(node))

        val listOfinput = listOfIdentifier.filter { name ->
            var declared = false
            for (variable in listOfDeclaredVariable) {
                if (name.contains(variable)) declared = true
            }
            !declared
        }

        fanin += HashSet(listOfinput).size

        return fanin
    }

    /**
     * Function to compute the FanOut of a Function (number of output)
     *
     *@return the FANOUT of [the function][node]
     */
    fun fanOutOfFunction(node: FunctionDef, listofCall: List<String>? = null, listOfGlobalVariable: List<String>?): Int {
        //name of the function
        val nameFunction = node.name.escapedCodeStr

        //listofcalls if none
        var listofCalls = listofCall
        if (listofCall == null) {
            listofCalls = ASTFunctions.listOfCallsOfAFunction(node)
        }

        //Doesn't count recurisivity
        listofCalls = listofCalls?.filter { name -> name != nameFunction }

        //Seems to require unique call and not all so HashSet
        var fanOut = HashSet(listofCalls).size

        //if return different of void then +1
        for (child in node.children) {
            if (child is ReturnType) {
                if (!child.escapedCodeStr.contains("void"))
                    fanOut++
            }
        }

        //Declaration statement
        val listOfParameters = ASTFunctions.listOfParameters(node)

        var listOfInterestingVariable: MutableList<String> = ArrayList(listOfGlobalVariable)
        listOfInterestingVariable.addAll(listOfParameters.filterValues { it }.keys)
        listOfInterestingVariable.addAll(ASTFunctions.listOfInterestingIdentifierDeclarationInAFunction(node, listOfInterestingVariable))
        //val listofUninterstingVariable = ASTFunctions.listofNotInterestingIdentifierDeclareofFunction(node, listOfInterestingVariable)

        var listOfExpression = ASTFunctions.listOfExpressionOfAFunction(node)
        listOfExpression = listOfExpression.filter { name ->
            var interest = false
            for (variable in listOfInterestingVariable) {
                if (name.startsWith(variable)) interest = true
            }
            interest
        }
        /** listOfExpression = listOfExpression.filter { name ->
        var interest = false
        for (variable in listofUninterstingVariable) {
        if (name.startsWith(variable)) interest = false
        }
        interest
        }*/
        fanOut += HashSet(listOfExpression).size

        return fanOut


    }
}