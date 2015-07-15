package de.lathspell.dtx2gp

import de.lathspell.dtx2gp.dtx.*
import java.util.HashMap
import java.util.Map
import java.util.TreeMap
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.apache.log4j.Logger
import org.herac.tuxguitar.song.factory.TGFactory
import org.herac.tuxguitar.song.managers.TGSongManager
import org.herac.tuxguitar.song.models.TGBeat
import org.herac.tuxguitar.song.models.TGChannel
import org.herac.tuxguitar.song.models.TGDuration
import org.herac.tuxguitar.song.models.TGMeasure
import org.herac.tuxguitar.song.models.TGNote
import org.herac.tuxguitar.song.models.TGSong
import org.herac.tuxguitar.song.models.TGTrack
import org.herac.tuxguitar.song.models.TGVoice

class DtxToTuxguitar {

    private val log = Logger.getLogger(this.javaClass)!!

    private val factory: TGFactory = TGFactory()

    fun convertSong(dtxfile: DTXFile): TGSong {
        // song
        val manager: TGSongManager = TGSongManager()
        val song: TGSong = manager.newSong()!!
        manager.setSong(song)
        song.setArtist(dtxfile.header.getArtist())
        song.setName(dtxfile.header.getTitle())

        // track
        assertEquals(1, song.countTracks())
        val track: TGTrack = song.getTrack(0)!!
        track.setName("Drums")
        TGChannel.setPercussionChannel(track.getChannel())
        assertTrue(track.isPercussionTrack())

        // build song structure
        for (i in song.countMeasureHeaders()..dtxfile.objects.objects.lastKey()!!) {
            manager.addNewMeasureBeforeEnd();
        }

        // TODO: this can varied using DTX lane #8
        var currentTempo = dtxfile.header.getBpm()

        // Iterate over all measures.
        // Dtx files sometimes omit some measures if they contain no notes, the empty measures
        // still need to get a tempo assigned though, else they are shown with the default of 120 bpm.
        for (measureNo : Int in 0..track.countMeasures() - 1) {
            log.info("Converting measure=$measureNo")

            // In DTX every instrument can have a different number of beats per measure, for TG we
            // have to count the maximum and use that.
            val dtxLanes: Map<Int, String> = dtxfile.objects.objects.get(measureNo) ?: HashMap<Int, String>()
            val numBeats: Int = countMaxNumBeats(dtxLanes)

            // Extend every DTX instrument line to the counted number of beats
            // Saved in extendedLines[voice][midi]=[1,0,1,1,0,0,1] bitmask.
            val extendedLanes: Map<Int, Map<Int, IntArray>> = extendLanes(dtxLanes, numBeats)

            // Save result in TuxGuitar data structure
            val measure: TGMeasure = track.getMeasure(measureNo)!!
            saveExtLinesInMeasure(measure, extendedLanes, numBeats, currentTempo)

            // The current version only compares two following notes so repeat it a couple of times.
            for (i in 0..5) {
                log.info("Beautification round $i")
                if (!beautifyMeasure(measure)) break
            }
        }

        return song
    }

    /** Tries to find adjacent notes/rests of the same kind and length and merges them.
     *
     * @return true if something was altered, false if nothing could be done
     */
    fun beautifyMeasure(tgMeasure: TGMeasure): Boolean {
        /*
        Beautification Algorithm:

        The converted notes do all have the same length and are filled with
        rests like e.g.:
            n8 n8 r8 n8 r8 r8 r8 r8

        Usually though one would write that like:
            n8 n4 -  n8 r4 -  n8 n8

        We start with trying a simple greedy algorithm that traverses each
        note of a measure and checks if there are enough rests to the right
        to promote the current note.
        As I don't like dotted notes, their possibility will not be checked.
        */
        // Prepare some Duration objects. 1/64 are sometimes used for flams.
        val durations = hashMap(
                64 to factory.newDuration(),
                32 to factory.newDuration(),
                16 to factory.newDuration(),
                8 to factory.newDuration(),
                4 to factory.newDuration(),
                2 to factory.newDuration(),
                1 to factory.newDuration()
        )
        for (i in durations.keySet()) {
            durations.get(i)!!.setValue(i)
        }

        val measure = tgMeasure.getNumber()
        log.debug("Beautifying measure=$measure")

        val numVoices = 2 // Well, yes, that's hardcoded...
        var wasChanged = false

        // Is this measure completely empty? Then add a rest as GuitarPro else shows 4 quarter rests.
        if (tgMeasure.countBeats() == 0) {
            log.debug("Filling empty measure with 1/1 rest")
            val newBeat = TGFactory().newBeat()!!
            for (v in 0..numVoices-1) {
                val voice = newBeat.getVoice(v)!!
                voice.setDuration(durations.get(1))
                voice.setEmpty(false)
            }
            tgMeasure.addBeat(newBeat)
        }

        // Voices are technically inside the beats but I can't think that way :-)
        // As the voice determines the duration, all notes inside the voice are promoted together.
        for (voice in 0..numVoices - 1) {
            log.debug("Beautifying measure=$measure, voice=$voice")

            // Iterate over all beats except the last (which can never be eligible for promotion).
            // As we wrote the beat objects ourselves we can be sure that they are ordered and also
            // all of the same length.
            // Foreign tracks sometimes have beats at unordered "start" positions.
            for (beat in 0..tgMeasure.countBeats() - 2) {
                val curTGVoice: TGVoice = tgMeasure.getBeat(beat)!!.getVoice(voice)!!
                val curLen: Int = curTGVoice.getDuration()!!.getValue()
                log.debug("Beautifying measure=$measure, voice=$voice, beat=$beat (len=$curLen)")

                // Rests, too, can be promoted but not empty beats.
                if (curTGVoice.isEmpty()) continue

                // And neither can whole notes.
                if (curLen == 1) continue

                // We only promote a note to e.g. a 1/2 if it is on beat 0 or 4 or to a 1/1 if it's on beat 0.
                val newTGDuration: TGDuration? = durations.get(curLen / 2)
                if (newTGDuration == null) {
                    throw Exception("Measure $measure, voice $voice, beat=$beat has invalid duration $curLen!");
                }
                if ((beat % (tgMeasure.countBeats() / newTGDuration.getValue())) != 0) {
                    log.debug("Promoting here would not be beautiful.")
                    continue
                }

                // Empty beats may follow but then there has to be a rest beat to promote this beat.
                var nextTGVoice: TGVoice? = null
                for (i in beat..tgMeasure.countBeats() - 2) {
                    nextTGVoice = tgMeasure.getBeat(i + 1)!!.getVoice(voice)!!
                    if (!nextTGVoice!!.isEmpty()) {
                        break
                    }
                }
                if (nextTGVoice == null || !nextTGVoice!!.isRestVoice()) continue
                val nextLen = nextTGVoice!!.getDuration()!!.getValue()
                log.debug("Found next rest:  voice=" + nextTGVoice!!.getIndex() + ", beat=? len=$nextLen isRest=" + nextTGVoice!!.isRestVoice())

                if (curLen != nextLen) {
                    log.debug("Current length $curLen, next length $nextLen => skipping")
                    continue
                }

                // Promote this one and mark the following  as empty
                log.debug("Promoting measure=$measure, beat=$beat, voice=$voice from $curLen to " + newTGDuration.getValue())
                curTGVoice.setDuration(newTGDuration)
                nextTGVoice!!.setEmpty(true)
                wasChanged = true
            }
        }

        return wasChanged
    }

    fun saveExtLinesInMeasure(tgMeasure: TGMeasure, extendedLanes: Map<Int, Map<Int, IntArray>>, numBeats: Int, currentTempo: Int) {
        // Currently all measures are in the same tempo.
        tgMeasure.getTempo()?.setValue(currentTempo);

        // Currently all notes are of the same length e.g. 1/8th.
        val tgDuration: TGDuration = factory.newDuration()!!
        tgDuration.setValue(numBeats)

        // Now map the DTX notes to TG notes of the correct voice.
        // BTW, preparing the TG data model in advance does not work as timer
        // values are not correctly calculated if the beats are all empty.
        // So we iterate beginning with the beat.
        for (beatNo in 0..numBeats - 1) {
            var tgBeat: TGBeat = factory.newBeat()!!
            tgBeat.setStart(beatNo * tgDuration.getTime())

            // Next we iterate over the two voices
            for (voiceNo in extendedLanes.keySet()) {
                val tgVoice: TGVoice = factory.newVoice(voiceNo)!!
                tgVoice.setDuration(tgDuration)

                // Iterate over all lanes i.e. MIDI instruments
                var string = 6
                for (midiNo in extendedLanes.get(voiceNo)?.keySet()) {
                    val beats = extendedLanes.get(voiceNo)?.get(midiNo)!!

                    // Add rest or note
                    val sample = beats.get(beatNo)

                    if (sample == 0) {
                        log.debug("Skipping note at beatNo=$beatNo, voiceNo=$voiceNo, midiNo=$midiNo: sample=$sample")
                    } else {
                        log.debug("Adding   note at beatNo=$beatNo, voiceNo=$voiceNo, midiNo=$midiNo: sample=$sample")
                        val note: TGNote = factory.newNote()!!
                        note.setValue(midiNo)
                        note.setString(string--)
                        tgVoice.addNote(note)
                    }
                }

                tgVoice.setEmpty(false) // if without notes then supposed to be a rest!
                tgBeat.setVoice(voiceNo, tgVoice)
            }
            tgMeasure.addBeat(tgBeat)
        }
    }

    fun countMaxNumBeats(lanes: Map<Int, String>): Int {
        var numBeats = 0
        for (laneNo : Int in lanes.keySet()) {
            // Special FX lanes should not be taken into account
            val midi: Int? = DTXInstruments.getLane(laneNo)?.midi
            if (midi == null) {
                continue
            }
            val dtxString: String = lanes.get(laneNo)!!
            if ((dtxString.length % 2) != 0) throw Exception("Length not a multiple of 2: |$dtxString|")
            val divisor = dtxString.length / 2
            if (divisor > numBeats) numBeats = divisor
        }
        return numBeats
    }

    fun extendLanes(lanes: Map<Int, String>, numBeats: Int): Map<Int, Map<Int, IntArray>> {
        // Initialize target data structure for two voices
        val extendedLanes: Map<Int, Map<Int, IntArray>> = TreeMap<Int, Map<Int, IntArray>>()
        extendedLanes.put(0, TreeMap<Int, IntArray>())
        extendedLanes.put(1, TreeMap<Int, IntArray>())

        // Iterate over all lanes
        for (laneNo : Int in lanes.keySet()) {
            // Some lanes are for Special FX or BPM changes and not instruments
            val midiNo: Int? = DTXInstruments.getLane(laneNo)?.midi
            if (midiNo == null) {
                log.info("Skipping lane $laneNo.")
                continue
            }
            val voiceNo = DTXInstruments.getMidi(midiNo).voice
            val dtxString: String = lanes.get(laneNo)!!

            // Splitting one line into the sample numbers which are not hex but to chars from 0-9A-Z.
            // "00020002" = 4th rest, 4th note with sample #02, 4th rest, 4th note with sample #02
            if ((dtxString.length % 2) != 0) throw Exception("Length not a multiple of 2: |$dtxString|")
            val numLaneBeats = dtxString.length / 2
            if ((numBeats % numLaneBeats) != 0) throw Exception("Odd number of $numLaneBeats lane beats for $numBeats TG beats!")
            val dtxLaneBeats = IntArray(numLaneBeats)
            for (i in 0..dtxLaneBeats.size - 1) {
                dtxLaneBeats[i] = Dec36().fromDec36(dtxString.substring(i * 2, i * 2 + 2))
            }

            // The dtxParts now contains "0,2,0,2" and must be extended to numBeats entries e.g. "0,0,2,0,0,0,2,0"
            // Sample numbers are irrelevant and could also just be converted to 0 or 1.
            val dtxExtLaneBeats = IntArray(numBeats)
            val laneBeatMod = numBeats / numLaneBeats
            for (i in 0..numBeats - 1) {
                if ((i % laneBeatMod) == 0) {
                    dtxExtLaneBeats[i] = dtxLaneBeats[i / laneBeatMod]
                } else {
                    dtxExtLaneBeats[i] = 0
                }
            }
            log.info("Extended voice=$voiceNo, midi=$midiNo: " + dtxExtLaneBeats.toList() + " from lane=$laneNo: $dtxString")

            // Save result
            extendedLanes.get(voiceNo)?.put(midiNo, dtxExtLaneBeats)
        }

        return extendedLanes
    }
}

