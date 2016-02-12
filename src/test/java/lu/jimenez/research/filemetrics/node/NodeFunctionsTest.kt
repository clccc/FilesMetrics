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

import ast.statements.CompoundStatement
import ast.statements.ExpressionStatement
import lu.jimenez.research.filemetrics.CodeMetricsTest
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldEqual
import java.util.*


class NodeFunctionsTest : Spek() {
    init {
        given("Different Inputs") {

            val inputAssertion = "x -> p = y + 1;"
            val inputDeclaration = "int z = 0; int c =  1;"
            val inputCall = "foo(a); foo(b); k(d);"
            val inputIdentifier = "int z = 0; foo(a,b);"
            val inputChained = "k = a = b = 1;"

            //First Identifier
            on("Trying to find the first identifier of Input Assertion") {
                val statement = CodeMetricsTest.parseAndWalk(inputAssertion)
                val firstId = NodeFunctions.findFirstIdentifier(statement)
                it("should return x") {
                    shouldEqual("x", firstId)
                }
            }
            on("Trying to find the first identifier of Input Call") {
                val statement = CodeMetricsTest.parseAndWalk(inputCall)
                val firstId = NodeFunctions.findFirstIdentifier(statement)
                it("should return foo") {
                    shouldEqual("foo", firstId)
                }
            }

            //Counting Variable
            on("Trying to count the number of Variables of Input Declaration") {
                val statement = CodeMetricsTest.parseAndWalk(inputDeclaration)
                val nbvariable = NodeFunctions.countingVariableOfASTNode(statement)
                it("should return 2") {
                    shouldEqual(2, nbvariable)
                }
            }
            on("Trying to count the number of Variables of Input Identifier") {
                val statement = CodeMetricsTest.parseAndWalk(inputIdentifier)
                val nbvariable = NodeFunctions.countingVariableOfASTNode(statement)
                it("should return 1") {
                    shouldEqual(1, nbvariable)
                }
            }

            //List Of calls
            on("Trying to list the calls made by Input Call") {
                val statement = CodeMetricsTest.parseAndWalk(inputCall)
                val listofCalls = NodeFunctions.listofCallInASTNode(statement)
                it("should return a list of size 3") {
                    shouldEqual(3, listofCalls.size)
                }
                it("should return be composed of 2 distinct element") {
                    shouldEqual(2, HashSet(listofCalls).size)
                }
            }
            on("Trying to list the calls made by Input Identifier") {
                val statement = CodeMetricsTest.parseAndWalk(inputIdentifier)
                val listofCalls = NodeFunctions.listofCallInASTNode(statement)
                it("should return a list of size 1") {
                    shouldEqual(1, listofCalls.size)
                }
            }

            //Interesting Term Present
            on("Trying to see if b and c  is present in input Identifier") {
                val statement = CodeMetricsTest.parseAndWalk(inputIdentifier)

                it("should return true for b") {
                    val blist = listOf("b")
                    val found = NodeFunctions.isAnyInteringTermPresent(statement, blist)
                    shouldEqual(true, found)
                }
                it("should return false for c") {
                    val clist = listOf("c")
                    val found = NodeFunctions.isAnyInteringTermPresent(statement, clist)
                    shouldEqual(false, found)
                }
            }
            on("Trying to see if b and c are present in input Chained") {
                val statement = CodeMetricsTest.parseAndWalk(inputChained)

                it("should return true for b") {
                    val blist = listOf("b")
                    val found = NodeFunctions.isAnyInteringTermPresent(statement, blist)
                    shouldEqual(true, found)
                }
                it("should return false for c") {
                    val clist = listOf("c")
                    val found = NodeFunctions.isAnyInteringTermPresent(statement, clist)
                    shouldEqual(false, found)
                }
            }

            //list of Identifier of a Node
            on("Listing all Identifier of Input Chained") {
                val statement = CodeMetricsTest.parseAndWalk(inputChained)
                val listId = NodeFunctions.listofMainIdentifierofANode(statement)
                it("should return a list of size 3") {
                    shouldEqual(3, listId.size)
                }
            }
            on("Listing all Identifier of Input Identifier") {
                val statement = CodeMetricsTest.parseAndWalk(inputIdentifier)
                val listId = NodeFunctions.listofMainIdentifierofANode(statement)
                it("should return a list of size 3") {
                    shouldEqual(3, listId.size)
                }
            }
            on("Listing all Identifier of Input Assertion") {
                val statement = CodeMetricsTest.parseAndWalk(inputAssertion)
                val listId = NodeFunctions.listofMainIdentifierofANode(statement)
                it("should return a list of size 2") {
                    shouldEqual(2, listId.size)
                }
            }

            //assignement on the right
            on("Listing assignement on the right of Imput chained") {
                val expression = (CodeMetricsTest.parseAndWalk(inputChained) as CompoundStatement).statements[0] as ExpressionStatement
                val listId = NodeFunctions.assignementOnTheRight(expression.expression)
                it("should return a list of size 3") {
                    shouldEqual(3, listId.size)
                }
            }
        }
    }
}