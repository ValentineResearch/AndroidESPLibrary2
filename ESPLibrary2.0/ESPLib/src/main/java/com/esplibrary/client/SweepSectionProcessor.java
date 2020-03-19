package com.esplibrary.client;

import com.esplibrary.data.SweepSection;
import com.esplibrary.packets.response.ResponseSweepSections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for assembling a list of all available sweep {@link SweepSection sections} based on
 * {@link ResponseSweepSections}.
 */
public class SweepSectionProcessor {

    public static final int MAX_SWEEP_SECTIONS = 15;
    private final SweepSection [] mSection;
    private int mSwpSectionCnt;
    private int mExpectedCnt = -1;

    public SweepSectionProcessor() {
        mSection = new SweepSection[MAX_SWEEP_SECTIONS];
    }

    /**
     * Adds a sweep section response packet received from the V1. Once all sweep sections have been received, a list of
     * all sweep sections is returned.
     *
     * @param resp Sweep Section response.
     *
     * @return List of {@link SweepSection sweep sections} once all sections response have been received.
     */
    public List<SweepSection> addSweepSection(ResponseSweepSections resp) {
        final int total = resp.getSweepSectionCount();
        final int containedCnt = resp.numberOfContainedSweepSections();
        final SweepSection[] sweepSections = resp.getSweepSections();
        // If we have enough sweep sections inside of this response, return them
        if(total <= containedCnt) {
            List<SweepSection> sections = new ArrayList<>(sweepSections.length);
            for (SweepSection section : sweepSections) {
                sections.add(section);
            }
            return sections;
        }
        else {
            // If the expected number of sweep sections is different, clear the old sweep sections
            // and set the expected count.
            if(mExpectedCnt != total) {
                clearSections();
                mExpectedCnt = total;
            }
            // Loop through the received sweep sections and add them to their correct index
            for (SweepSection section : sweepSections) {
                // The sweep range index isn't zero based.
                int sectionIdx = section.getIndex() - 1;
                mSection[sectionIdx] = section;
                mSwpSectionCnt++;
            }
            if(mExpectedCnt == mSwpSectionCnt) {
                List<SweepSection> sections = new ArrayList<>();
                // Add the rec. sweep sections to the new list.
                for (SweepSection section : mSection) {
                    if (section != null) {
                        sections.add(section);
                    }
                }
                // Clear state
                clearSections();
                return sections;
            }
        }
        return null;
    }

    /**
     * Clear the contained sweep sections data.
     */
    public void reset() {
        clearSections();
    }

    /**
     * Clears the contained sweep sections data.
     */
    private void clearSections() {
        // null all indices.
        Arrays.fill(mSection, null);
        // set the sweep range count to zero.
        mSwpSectionCnt = 0;
        mExpectedCnt = 0;
    }
}
