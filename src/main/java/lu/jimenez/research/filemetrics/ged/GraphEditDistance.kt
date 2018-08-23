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

import lu.jimenez.research.filemetrics.ged.graph.Edge
import lu.jimenez.research.filemetrics.ged.graph.Graph
import lu.jimenez.research.filemetrics.ged.graph.Node

class GraphEditDistance {
    var costMatrix: Array<DoubleArray>
    val substituteCost: Double
    val insertCost: Double
    val deletCost: Double
    val g1: Graph
    val g2: Graph
    val levenstein: Boolean


    constructor(SubstituteCost: Double, InsertionCost: Double, DeletionCost: Double, g2: Graph, g1: Graph, levenstein: Boolean) {

        this.substituteCost = SubstituteCost
        this.insertCost = InsertionCost
        this.deletCost = DeletionCost
        this.g2 = g2
        this.g1 = g1
        this.levenstein = levenstein
        costMatrix = createCostMatrix()

    }

    fun createCostMatrix(): Array<DoubleArray> {
        val sizeG1: Int = g1.nodes.size
        val sizeG2: Int = g2.nodes.size
        val costMatrix = Array(sizeG1 + sizeG2, { DoubleArray(sizeG1 + sizeG2) })

        for (i in 0..sizeG1 - 1) {
            for (j in 0..sizeG2 - 1)
                costMatrix[i][j] = getSubstitutionCost(g1.nodes[i], g2.nodes[j])
        }
        for (i in 0..sizeG2 - 1) {
            for (j in 0..sizeG2 - 1)
                costMatrix[i + sizeG1][j] = getInsertionCost(i, j)
        }
        for (i in 0..sizeG1 - 1) {
            for (j in 0..sizeG1 - 1)
                costMatrix[i][j + sizeG2] = getDeletiontionCost(i, j)
        }
        return costMatrix
    }

    private fun getInsertionCost(i: Int, j: Int): Double {
        if (i == j) {
            return getPosWeight(g2.nodes[j]) * insertCost
        }
        return Double.MAX_VALUE
    }

    private fun getPosWeight(node: Node): Double {
        if (levenstein) {
            return node.label.length.toDouble()
        } else
            return 1.0

    }

    private fun getPosWeight(node1: Node, node2: Node): Double {
        if (levenstein) {
            return node1.levenshtein(node2).toDouble()
        }
        return 1.0
    }

    private fun getDeletiontionCost(i: Int, j: Int): Double {
        if (i == j) {
            return getPosWeight(g1.nodes[i]) * deletCost
        }
        return Double.MAX_VALUE
    }

    private fun getSubstitutionCost(node1: Node, node2: Node): Double {
        val diff = (getRelabelCost(node1, node2) + getEdgeDiff(node1, node2)) / 2.0
        return diff * substituteCost
    }

    fun getEdgeDiff(node1: Node, node2: Node): Double {
        val edges1: List<Edge> = g1.edges[node1]!!
        val edges2: List<Edge> = g2.edges[node2]!!
        val e1Size = edges1.size
        val e2Size = edges2.size
        if (e1Size == 0 || e2Size == 0) {
            return (edges1.size + edges2.size).toDouble()
        }
        val edgeCostMatrix = Array(e1Size + e2Size, { DoubleArray(e1Size + e2Size) })

        for (i in 0..e1Size - 1) {
            for (j in 0..e2Size - 1) {
                edgeCostMatrix[i][j] = getEdgeEditCost(edges1[i], edges2[j])
            }
        }
        for (i in 0..e2Size - 1) {
            for (j in 0..e2Size - 1) {
                edgeCostMatrix[i + e1Size][j] = getEdgeInsertCost(i, j)
            }
        }
        for (i in 0..e1Size - 1) {
            for (j in 0..e2Size - 1) {
                edgeCostMatrix[j][i + e2Size] = getEdgeDeleteCost(i, j)
            }
        }

        val assignment = HungarianAlgorithm.hgAlgorithm(edgeCostMatrix, "min")
        var sum = 0.0
        for (i in assignment.indices) {
            sum += edgeCostMatrix[assignment[i][0]][assignment[i][1]]
        }
        return sum / ((e1Size + e2Size))
    }

    fun getEdgeInsertCost(i: Int, j: Int): Double {
        if (i == j) {
            return insertCost
        }
        return java.lang.Double.MAX_VALUE
    }

    fun getEdgeDeleteCost(i: Int, j: Int): Double {
        if (i == j) {
            return deletCost
        }
        return java.lang.Double.MAX_VALUE
    }

    fun getEdgeEditCost(edge1: Edge, edge2: Edge): Double {
        if (edge1.equals(edge2))
            return 0.0
       // if (edge1.from.equals(edge2.from) || edge1.ton.equals(edge2.ton))
         //   return 1.0
        return 1.0
    }

    private fun getRelabelCost(node1: Node, node2: Node): Double {
        if (!node1.equals(node2)) {
            return getPosWeight(node1, node2)
        }
        return 0.0
    }


    fun getNormalizedDistance(): Double {
        val graphLength: Double = (g1.size() + g2.size()) / 2.0
        return getDistance() / graphLength
    }

    fun getDistance(): Double {
        val assignement = HungarianAlgorithm.hgAlgorithm(costMatrix, "min")
        var sum: Double = 0.0
        for (i in 0..assignement.size - 1) {
            sum += costMatrix[assignement[i][0]][assignement[i][1]]
        }
        return sum
    }

}


 