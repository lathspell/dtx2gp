package DTX

import java.util.Map
import java.util.HashMap
import java.util.LinkedHashMap

/** The header of a DTX file. */
public class DTXHeader {
    val attrs : Map<String, String> = LinkedHashMap<String, String>()

    public fun put(name : String, value : String) {
        attrs.put(name, value)
    }

    public fun get(key : String) : String {
        return attrs.get(key)!!
    }

    public fun getTitle() : String {
        return attrs.get("TITLE")!!
    }

    public fun getArtist() : String {
        return attrs.get("ARTIST")!!
    }

    public fun getBpm() : Int {
        // Due to a locale bug, the decimal dot is sometimes a comma
        return attrs.get("BPM")!!.replace(',', '.').toFloat().toInt()
    }

    public fun toString() : String {
        return String.format("%s - %s (@%s bpm)", getArtist(), getTitle(), getBpm())!!
    }

    public fun dump() : String {
        var dump = ""
        for (key in attrs.keySet()) {
            dump += String.format("#%s: %s\n", key, attrs.get(key))
        }
        return dump
    }
}
