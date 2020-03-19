package com.esplibrary.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure representing all available Sweep Data from the attached V1
 */
public class SweepData {
    /**
     * Maximum number of Sweeps the V1 supports.
     */
    public int maxSweepIndex;
    /**
     * Sweep Sections the V1 use.
     */
    public List<SweepSection> sweepSections;
    /**
     * Default Sweep definition. If the v1 doesn't default Sweep definitions this will be null.
     */
    public List<SweepDefinition> defaultDefinitions;
    /**
     * V1's current Sweep definition
     */
    public List<SweepDefinition> customSweeps;

    public SweepData(int maxSweepIndex, List<SweepSection> sweepSections, List<SweepDefinition> defaultDefinitions, List<SweepDefinition> customSweeepDefinitions) {
        this.maxSweepIndex = maxSweepIndex;
        this.sweepSections = sweepSections;
        this.defaultDefinitions = defaultDefinitions;
        this.customSweeps = customSweeepDefinitions;
    }

    private SweepData(SweepData self) {
        this.maxSweepIndex = self.maxSweepIndex;
        this.sweepSections = cloneSweepSections(self.sweepSections);
        this.defaultDefinitions = cloneSweepDefinitions(self.defaultDefinitions);
        this.customSweeps = cloneSweepDefinitions(self.customSweeps);
    }

    /**
     * Creates a deep-copy of this instance.
     *
     * @return New instance with a deep-copy if this instances data
     */
    public SweepData clone() {
        return new SweepData(this);
    }

    /**
     * Utility method for creating a clone of a list of sweep sections.
     *
     * @param sweepSections Original list of sweep sections
     *
     * @return Deep-copy clone of sweepSections
     */
    public static List<SweepSection> cloneSweepSections(List<SweepSection> sweepSections) {
        List<SweepSection> clones = new ArrayList<>();
        for (int i = 0; i < sweepSections.size(); i++) {
            clones.add(sweepSections.get(i).clone());
        }
        return clones;
    }

    /**
     * Utility method for creating a clone of a list of sweep {@link SweepDefinition definitions}.
     *
     * @param sweepDefinitions Original list of sweep sections
     *
     * @return Deep-copy clone of sweepDefinitions
     */
    public static List<SweepDefinition> cloneSweepDefinitions(List<SweepDefinition> sweepDefinitions) {
        List<SweepDefinition> clones = new ArrayList<>();
        for (int i = 0; i < sweepDefinitions.size(); i++) {
            clones.add(sweepDefinitions.get(i).clone());
        }
        return clones;
    }
}
