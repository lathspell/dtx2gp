package DTX

import java.util.ArrayList
import java.util.List

/** Mapping between DTXMania Drum Lanes and General MIDI Percussion instruments.
*
* - http://mainori-se.sakura.ne.jp/dtxmania/wiki.cgi?page=qa_dtx_spec_e
* - http://en.wikipedia.org/wiki/General_MIDI#Percussion
*
*/
class DTXInstrument(val name: String, val lane: Int, val midi: Int, val voice: Int)

object DTXInstruments {
    val all: List<DTXInstrument> = ArrayList<DTXInstrument>();
    {
        all.add(DTXInstrument("HiHatClose", 0x11, 42, 0))
        all.add(DTXInstrument("Snare", 0x12, 38, 1))
        all.add(DTXInstrument("Bass", 0x13, 35, 1))
        all.add(DTXInstrument("HighTom", 0x14, 47, 0))
        all.add(DTXInstrument("LowTom", 0x15, 45, 0))
        all.add(DTXInstrument("Crash2", 0x16, 57, 0))
        all.add(DTXInstrument("FloorTom", 0x17, 43, 0))
        all.add(DTXInstrument("HiHatOpen", 0x18, 46, 0))
        all.add(DTXInstrument("Ride", 0x19, 51, 0))
        all.add(DTXInstrument("Crash1", 0x1A, 49, 0))
    }

    fun getMidi(midi: Int): DTXInstrument {
        for (i in all) {
            if (i.midi == midi) {
                return i
            }
        }
        throw IllegalArgumentException("Cannot map midi instrument $midi to DTX lane!");
    }

    fun getLane(lane: Int): DTXInstrument? {
        val found = all.filter( { it.lane == lane } )
        return if (found.size() == 0) null else found.first()
    }
}
