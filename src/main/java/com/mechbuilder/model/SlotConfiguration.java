package com.mechbuilder.model;

public class SlotConfiguration {
    private final String mechSize;
    private final int leftArmSlots;
    private final int leftTorsoSlots;
    private final int cockpitSlots;
    private final int centerTorsoSlots;
    private final int rightTorsoSlots;
    private final int rightArmSlots;
    private final int leftLegSlots;
    private final int rightLegSlots;

    public SlotConfiguration(String mechSize, int leftArmSlots, int leftTorsoSlots, 
                           int cockpitSlots, int centerTorsoSlots, int rightTorsoSlots,
                           int rightArmSlots, int leftLegSlots, int rightLegSlots) {
        this.mechSize = mechSize;
        this.leftArmSlots = leftArmSlots;
        this.leftTorsoSlots = leftTorsoSlots;
        this.cockpitSlots = cockpitSlots;
        this.centerTorsoSlots = centerTorsoSlots;
        this.rightTorsoSlots = rightTorsoSlots;
        this.rightArmSlots = rightArmSlots;
        this.leftLegSlots = leftLegSlots;
        this.rightLegSlots = rightLegSlots;
    }

    public String getMechSize() { return mechSize; }
    public int getLeftArmSlots() { return leftArmSlots; }
    public int getLeftTorsoSlots() { return leftTorsoSlots; }
    public int getCockpitSlots() { return cockpitSlots; }
    public int getCenterTorsoSlots() { return centerTorsoSlots; }
    public int getRightTorsoSlots() { return rightTorsoSlots; }
    public int getRightArmSlots() { return rightArmSlots; }
    public int getLeftLegSlots() { return leftLegSlots; }
    public int getRightLegSlots() { return rightLegSlots; }

    @Override
    public String toString() {
        return String.format("SlotConfiguration{%s: LA=%d, LT=%d, C=%d, CT=%d, RT=%d, RA=%d, LL=%d, RL=%d}",
                mechSize, leftArmSlots, leftTorsoSlots, cockpitSlots, centerTorsoSlots,
                rightTorsoSlots, rightArmSlots, leftLegSlots, rightLegSlots);
    }
}