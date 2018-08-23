/////////////////////////////////////////////////////////////////////////////////////////
//                 University of Luxembourg  -
//                 Interdisciplinary center for Security and Trust (SnT)
//                 Copyright © 2016 University of Luxembourg, SnT
//
//
//  Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
//
//
//    Author: Matthieu Jimenez – SnT – matthieu.jimenez@uni.lu
//
//////////////////////////////////////////////////////////////////////////////////////////
package lu.jimenez.research.filemetrics.ged

import ast.ASTNode
import ast.functionDef.FunctionDef
import cfg.C.CCFGFactory
import lu.jimenez.research.filemetrics.ged.graph.Graph
import lu.jimenez.research.filemetrics.ged.graph.Node


class CGFEditDistance(version1: List<ASTNode>, version2: List<ASTNode>, val levensthein: Boolean) {
    val mapFunction: MutableMap<String, MutableList<Graph>>

    init {
        mapFunction = mutableMapOf<String, MutableList<Graph>>()

        for (node in version1) {
            if (node is FunctionDef) {
                val graph = graphTranslation(node)
                if (mapFunction.containsKey(node.functionSignature)) {
                    var i = 0
                    while (mapFunction.containsKey(node.functionSignature + "_$i")) i++
                    mapFunction.put(node.functionSignature + "_$i", mutableListOf(graph))
                } else
                    mapFunction.put(node.functionSignature, mutableListOf(graph))
            }
        }
        for (node in version2) {
            if (node is FunctionDef) {
                val graph = graphTranslation(node)
                if (mapFunction.containsKey(node.functionSignature)) {
                    if (mapFunction[node.functionSignature]!!.size > 1) {
                        var i = 0
                        while (mapFunction[node.functionSignature + "_$i"]!!.size > 1) i++
                        mapFunction[node.functionSignature + "_$i"]!!.add(graph)
                    } else mapFunction[node.functionSignature]!!.add(graph)
                } else mapFunction.put(node.functionSignature, mutableListOf(graph))
            }
        }
    }

    fun computeEditDistance(): Double {
        var editDistance: Double = 0.0
        val editcost :Double= if (levensthein) 1.0 else 2.0
        for ((key, value) in mapFunction) {
            when (value.size) {
                1 -> {
                    val ged = GraphEditDistance(editcost, 1.0, 1.0, value[0], Graph(), levensthein)
                    editDistance += ged.getDistance()
                }
                2 -> {
                    val ged = GraphEditDistance(editcost, 1.0, 1.0, value[1], value[0], levensthein)
                    editDistance += ged.getDistance()
                }
                else -> throw ArrayIndexOutOfBoundsException("function name duplicate in file")
            }
        }
        return editDistance
    }

    fun graphTranslation(node: FunctionDef): Graph {
        val graph = Graph()
        val cfg = CCFGFactory.convert(node.content)
        for (edge in cfg.edges) {
            val nodeSource = Node(edge.source.toString())
            val nodeDestination = Node(edge.destination.toString())
            graph.addEdge(nodeSource, nodeDestination)
        }
        return graph
    }

}


 