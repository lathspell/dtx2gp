package DTX

import java.util.Map
import java.util.List
import java.util.ArrayList
import java.util.HashMap
import java.util.SortedSet
import java.util.SortedMap
import java.util.TreeMap
import org.apache.log4j.Logger

class DTXObjects() {
    val log = Logger.getLogger(this.javaClass)!!

    public val objects : TreeMap<Int, TreeMap<Int, String>> = TreeMap<Int, TreeMap<Int, String>>()

    fun add(bar : Int, lane : Int, value : String) {
        objects.getOrPut(bar) { TreeMap<Int,String>() }.put(lane, value)
    }

    fun get(bar : Int, lane : Int) : String {
        return objects.get(bar)?.get(lane)!!
    }

    fun dump() : String {
        var dump = ""

        for (barNr in objects.keySet()) {
            val bar = objects.get(barNr)!!
            for (laneNr in bar.keySet()) {
                dump += String.format("#%03d%02X: %s\n", barNr, laneNr, bar.get(laneNr))
            }
        }

        return dump
    }
}
