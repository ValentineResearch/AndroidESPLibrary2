package com.esplibrary.client;

import com.esplibrary.data.SweepDefinition;
import com.esplibrary.data.SweepSection;
import com.esplibrary.packets.response.ResponseSweepSections;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for assembly a list of sweep {@link SweepDefinition definitions}.
 */
public class SweepDefinitionProcessor {

    private final int mMaxSweeps;
    private final SweepDefinition [] mSweeps;
    private int mSweepMask;
    private int mRxSweeps;

    /**
     * Initializes a sweep processor for the specified number of sweeps definitions
     *
     * @param maxSweeps Maximum number of sweep definitions
     */
    public SweepDefinitionProcessor(int maxSweeps) {
        mMaxSweeps = maxSweeps;
        mSweeps = new SweepDefinition[mMaxSweeps];
        // Set a max of
        mSweepMask = (1 << maxSweeps) - 1;
    }

    /**
     * Adds a {@link SweepDefinition} received from the V1. Once all definitions have been received, a
     * list of {@link SweepDefinition definition} is returned.
     *
     * @return  List of {@link SweepDefinition definition} once all sweep definitions have been
     * received.
     */
    public List<SweepDefinition> addSweepDefinition(SweepDefinition definition) {
        final int sweepIdx = definition.getIndex();
        mSweeps[sweepIdx] = definition;
        // Set the received sweep.
        mRxSweeps |= (0x01 << sweepIdx);
        // If the received sweeps equal the sweep max return the sweep list.
        if(mRxSweeps == mSweepMask) {
            List<SweepDefinition> sweeps = new ArrayList<>();
            for (SweepDefinition sweep : mSweeps) {
                sweeps.add(sweep);
            }
            return sweeps;
        }
        return null;
    }
}
