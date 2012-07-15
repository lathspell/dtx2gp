package de.lathspell.dtx2gp.tg

import java.io.File
import org.apache.commons.io.FileUtils
import org.herac.tuxguitar.song.models.TGBeat
import org.herac.tuxguitar.song.models.TGDuration
import org.herac.tuxguitar.song.models.TGMeasure
import org.herac.tuxguitar.song.models.TGMeasureHeader
import org.herac.tuxguitar.song.models.TGNote
import org.herac.tuxguitar.song.models.TGSong
import org.herac.tuxguitar.song.models.TGTempo
import org.herac.tuxguitar.song.models.TGTimeSignature
import org.herac.tuxguitar.song.models.TGTrack
import org.herac.tuxguitar.song.models.TGVoice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GP5ReadTest1Test {

    [Test]
    fun loadTest1() {
        val song: TGSong = TGHelper().readGP5Song("data/test1.gp5")
        assertEquals("Testsong1", song.getName())
        assertEquals("Testartist1", song.getArtist())
        assertEquals("Testalbum1", song.getAlbum())
        assertEquals("", song.getComments())
        assertEquals(1, song.countTracks())
        assertEquals(2, song.countMeasureHeaders())

        val track: TGTrack = song.getTrack(0)!!
        assertEquals("Drumkit", track.getName())
        assertTrue(track.isPercussionTrack())
        assertFalse(track.isMute())
        assertEquals(2, track.countMeasures())

        val m0: TGMeasure = track.getMeasure(0)!!
        assertEquals(1, m0.getNumber()) // Measure number counting from 1
        val timesig: TGTimeSignature = m0.getTimeSignature()!!
        assertEquals(4, timesig.getNumerator())
        assertEquals(4, timesig.getDenominator()?.getValue())
        val tempo: TGTempo = m0.getTempo()!!
        assertEquals(123, tempo.getValue())
        assertEquals(487, tempo.getInMillis())
        assertEquals(3840, m0.getLength()) // ???
        assertEquals(TGDuration.QUARTER_TIME, m0.getStart())
        assertEquals(TGMeasure.DEFAULT_KEY_SIGNATURE, m0.getKeySignature())
        assertEquals(TGMeasure.CLEF_TREBLE, m0.getClef())
        assertEquals(TGMeasureHeader.TRIPLET_FEEL_NONE, m0.getTripletFeel())
        assertEquals(3, m0.countBeats())

        val beat0: TGBeat = m0.getBeat(0)!!
        assertEquals(2, beat0.countVoices())
        assertFalse(beat0.isRestBeat())
        assertNull(beat0.getText())

        val v0: TGVoice = beat0.getVoice(0)!!
        assertEquals(0, v0.getIndex())
        assertEquals(2, v0.countNotes())
        assertEquals(TGVoice.DIRECTION_NONE, v0.getDirection())
        assertEquals(TGDuration.QUARTER, v0.getDuration()?.getValue())

        val note0: TGNote = v0.getNote(0)!!
        assertEquals(5, note0.getString()) // sheet line
        assertEquals(38, note0.getValue()) // MIDI instrument
        assertEquals(95, note0.getVelocity()) // dynamic
        assertFalse(note0.getEffect()?.hasAnyEffect()!!)
    }

    [Test]
    fun dumpCompactTest() {
        val song = TGHelper().readGP5Song("data/test1.gp5")
        val dump: String = TGHelper().dumpCompactTGSong(song)

        val dumpOk = "Testartist1 - Testsong1 (Album: Testalbum1)\n" +
        "Track 1 \"Drumkit\" (percussion)\n" +
        "  Measure 1 (4/4 123bpm)\n" +
        "    Beat 0: (¼) 38,42       (¼) 35\n" +
        "    Beat 1: (¼) 42          (¼) 35\n" +
        "    Beat 2: (½) rest        (½) rest\n" +
        "  Measure 2 (4/4 123bpm)\n"

        println("Dumping test1.gp5:\n" + dump)
        assertEquals(dumpOk, dump)
    }

    [Test]
    fun dumpTest1() {
        val song = TGHelper().readGP5Song("data/test1.gp5")
        val dump: String = TGHelper().dumpTGSong(song)
        FileUtils.writeStringToFile(File("data/test1.dump"), dump)
        val dumpOk = FileUtils.readFileToString(File("data/test1.dump.ok"))
        assertEquals(dumpOk, dump)
    }

}
