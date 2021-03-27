package net.flytre.flytre_lib.client.gui;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A toggle button takes a 16x64 image and displays 4 states from top to bottom: (state 0, state 0 hovered, state 1,
 * state 1 hovered) based on the circumstances. Predictably used to toggle between 2 states, ie input and output or
 * round robin mode and normal mode.
 */
public class ToggleButton extends MultistateButton {


    /**
     * Instantiates a new Toggle button.
     *
     * @param x       the x
     * @param y       the y
     * @param width   the width
     * @param height  the height
     * @param state   0 or 1 for 1st / 2nd frame
     * @param texture the texture location
     * @param onPress what to do when the button is pressed
     * @param id      text to overlay on button
     */
    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, char id) {
        this(x, y, width, height, state, texture, onPress, id + "");
    }

    /**
     * Instantiates a new Toggle button.
     *
     * @param x       the x
     * @param y       the y
     * @param width   the width
     * @param height  the height
     * @param state   0 or 1 for 1st / 2nd frame
     * @param texture the texture location
     * @param onPress what to do when the button is pressed
     * @param id      text to overlay on button
     */
    public ToggleButton(int x, int y, int width, int height, int state, Identifier texture, PressAction onPress, String id) {
        super(x, y, width, height, state, 2, texture, onPress, id);
    }


    /**
     * Sets tooltips, make sure you set the renderer. See the ButtonTooltipRenderer for the easiest way
     * to do this
     *
     * @param frame1 the frame 1
     * @param frame2 the frame 2
     */
    public void setTooltips(Text frame1, Text frame2) {
        super.setTooltips(frame1, frame2);
    }

    /**
     * Flip between state 0 / 1
     */
    public void toggleState() {
        this.cycleState();
    }

}

