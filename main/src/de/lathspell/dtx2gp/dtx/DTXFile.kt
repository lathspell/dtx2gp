package de.lathspell.dtx2gp.dtx

/** Representation of a DTX file. */
public class DTXFile() {
    val header : DTXHeader = DTXHeader()

    val objects : DTXObjects = DTXObjects()

    public fun toString() : String {
        return header.toString()
    }

    public fun toText() : String {
        return header.dump() + "\n" + objects.dump()
    }
}
