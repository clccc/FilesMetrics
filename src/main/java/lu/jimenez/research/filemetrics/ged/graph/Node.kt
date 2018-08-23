package lu.jimenez.research.filemetrics.ged.graph


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
    
 class Node(val label: String) {


     fun levenshtein(otherNode: Node) : Int {
         val lhsLenght = label.length
         val rhsLenght = otherNode.label.length

         var cost = Array(lhsLenght) { it }
         var newCost = Array(lhsLenght) { 0 }

         for (i in 1..rhsLenght-1) {
             newCost[0] = i

             for (j in 1..lhsLenght-1) {
                 val match = if(label[j - 1] == otherNode.label[i - 1]) 0 else 2

                 val costReplace = cost[j - 1] + match
                 val costInsert = cost[j] + 1
                 val costDelete = newCost[j - 1] + 1

                 newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
             }

             val swap = cost
             cost = newCost
             newCost = swap
         }

         return cost[lhsLenght - 1]
     }

     override fun equals(other: Any?): Boolean{
         if (this === other) return true
         if (other?.javaClass != javaClass) return false

         other as Node

         if (label != other.label) return false

         return true
     }

     override fun hashCode(): Int{
         return label.hashCode()
     }


 }