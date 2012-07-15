package de.lathspell.dtx2gp.dtx

import org.apache.log4j.*

/**
* Reads a .dtx (DTXMania drum track) file.
*/
class DTXReader() {
    val log = Logger.getLogger(this.javaClass)!!

    public fun readText(text : String) : DTXFile {
        val dtxfile : DTXFile = DTXFile()
        val header = dtxfile.header
        val headerAttrs = header.attrs
        val objects = dtxfile.objects

        for (val line : String in text.split("\n").map { it.trim() }) {

            if (line.matches("^(;.*|)$")) {
                log.debug("Parsed comment line: $line")
                continue;
            } else if (line.matches("^#([A-Z0-9_]+):(.*)")) {
                val tmp = line.substring(1).split(":")
                val key = tmp[0]
                val value = if (tmp.size == 2) tmp[1].trim() else ""
                if (key.matches("^(\\d\\d\\d)([0-9A-F]{2})$")) {
                    val bar = Integer.parseInt(key.substring(0, 3), 10)
                    val lane = Integer.parseInt(key.substring(3, 5), 16)
                    log.info("Parsed object line $key: (bar $bar, lane $lane) $value")
                    objects.add(bar, lane, value)
                } else {
                    log.debug("Parsed header line $key: $value")
                    headerAttrs.put(key, value)
                }
            } else {
                throw Exception("Parser error at line: |$line|")
            }
        }

        log.info("Loaded $dtxfile")

        return dtxfile
    }
}
