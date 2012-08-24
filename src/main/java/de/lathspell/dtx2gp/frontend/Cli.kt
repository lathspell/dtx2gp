package de.lathspell.dtx2gp.frontend

import java.io.File

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.herac.tuxguitar.song.models.TGSong

import de.lathspell.dtx2gp.DtxToTuxguitar
import de.lathspell.dtx2gp.dtx.DTXFile
import de.lathspell.dtx2gp.dtx.DTXReader
import de.lathspell.dtx2gp.tg.TGHelper

/** CLI Frontend */
fun main(args : Array<String>) {
    if (args.size != 2) {
        println("Usage: dtx2gp <input> <output>\n")
        System.exit(1)
    }
    val fnameIn = args[0]
    val fnameOut = args[1]

    val log = Logger.getRootLogger()!!
    log.addAppender(ConsoleAppender(PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)))

    log.info("Loading $fnameIn")
    val text = File(fnameIn).readText("UTF-8")

    log.info("Converting...")
    val dtxfile: DTXFile = DTXReader().readText(text)
    val tgsong: TGSong = DtxToTuxguitar().convertSong(dtxfile)

    log.info("Writing $fnameOut")
    TGHelper().writeGP5Song(fnameOut, tgsong)

    log.info("Finished.")
}
