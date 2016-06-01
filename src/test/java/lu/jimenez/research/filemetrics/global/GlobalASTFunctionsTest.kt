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
import lu.jimenez.research.filemetrics.CodeMetricsTest
import lu.jimenez.research.filemetrics.ast.VisitingAST
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeTrue
import org.jetbrains.spek.api.shouldEqual


class GlobalASTFunctionsTest : Spek() {

    init {
        given("The AddC program") {
            val addC =
                    """
            /* add.c
            * a simple C program
            */

            #include <stdio.h>
            #define LAST 10
            int k = 0;

            int main()
            {
                int i, sum = 0;
                int  *ip;        /* pointer variable declaration */

                ip = &i;  /* store address of var in pointer variable*/
                for ( i = 1; i <= LAST; i++ ) {
                sum += i;
            } /*-for-*/
            printf("sum = %d\n", sum);

            return 0;
            }"""

            val listOfNode = CodeMetricsTest.parseAndWalkModule(addC)
            //CodeMetricsTest.tryToWork(listOfNode)
            for(i in listOfNode)
                VisitingAST.whenASTNode(i)
            val lex = CodeMetricsTest.parseModule(addC)
            print(lex.allTokens)

            on("listing the function") {
                val functionList = GlobalASTFunctions.listofFunctionName(listOfNode)
                it("should return a list of size one") {
                    shouldEqual(1, functionList.size)
                }
                it("containing main") {
                    shouldEqual("main", functionList[0])
                }
            }

            on("listing variable") {
                val listvariable = GlobalASTFunctions.listofGlobalVariable(listOfNode)
                it("should return a list of size 1") {
                    shouldEqual(1, listvariable.size)
                }
                it("containing k") {
                    shouldEqual("k", listvariable[0])
                }

            }

            on("listing the calls made by functions") {
                val mapofCall = GlobalASTFunctions.mapOfCallMadeByFunctions(listOfNode)
                it("should return a map of size 1") {
                    shouldEqual(1, mapofCall.size)
                }
                it("containin the key main") {
                    shouldBeTrue(mapofCall.containsKey("main"))
                }
                it("that should contains the value printf") {
                    shouldEqual("printf", (mapofCall["main"]!!)[0])
                }
            }
        }
    }


}