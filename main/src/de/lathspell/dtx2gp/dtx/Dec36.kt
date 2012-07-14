import java.util.ArrayList

/** FIXME: Should be an "object class" i.e. with "static methods". */
class Dec36 {

    val list = ArrayList<Char>()

    constructor {
        for (i in 0..9) list.add(('0'.toInt() + i).toChar())
        for (i in 0..25) list.add(('A'.toInt() + i).toChar())
    }

    fun toDec36(v: Int): String {
        return list.get(v / 36).toString() + list.get(v % 36).toString()
    }

    fun fromDec36(s: String): Int {
        return list.indexOf(s.charAt(0)) * 36 + list.indexOf(s.charAt(1))
    }


}
