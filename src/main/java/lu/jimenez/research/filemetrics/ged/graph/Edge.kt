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
    
 class Edge(val from: Node,val ton :Node) {

     override fun equals(other: Any?): Boolean {
         if(other is Edge)
            return from.equals(other.from) && ton.equals(other.ton)
         return false
     }

     override fun hashCode(): Int {
         return super.hashCode()
     }
 }