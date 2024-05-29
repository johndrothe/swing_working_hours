package org.rothe.john.working_hours.ui.canvas.shifts;

import lombok.val;
import org.rothe.john.working_hours.ui.canvas.Canvas;
import org.rothe.john.working_hours.ui.canvas.CanvasInfo;
import org.rothe.john.working_hours.ui.canvas.shifts.calculator.ShiftCalculator;

import javax.swing.*;
import java.awt.*;

import static java.util.Objects.isNull;

public class ShiftPainter {
    private final Canvas canvas;
    private final CanvasInfo canvasInfo;
    private ShiftCalculator table;

    public ShiftPainter(Canvas canvas, CanvasInfo canvasInfo) {
        this.canvas = canvas;
        this.canvasInfo = canvasInfo;
        this.table = null;
    }

    public void initialize() {
        if (isNull(canvas.getTeam())) {
            this.table = null;
        } else {
            this.table = ShiftCalculator.of(canvas.getTeam().getMembers());
        }
    }

    // TODO: Convert this logic to use SpaceTime's split around the same way MemberRow does.
    // TODO: Consider creating shifts for contiguous segments with the same team members so that the user can choose which to display.


    public void paintUnder(Graphics2D g2d, JPanel exampleRow) {
        val target = new PaintTarget(canvasInfo, g2d, toRowStartX(exampleRow));
        val canvasHeight = canvas.getHeight();

        table.largestShift().forEach(s -> target.fillShift(s.time(), canvasHeight));
        table.shiftChanges().forEach(s -> target.drawLine(s.time().totalMinutesInUtc(), canvasHeight));
    }

    public void paintOver(Graphics2D g2d, JPanel exampleRow) {
        val target = new PaintTarget(canvasInfo, g2d, toRowStartX(exampleRow));

        table.largestShift().forEach(s -> target.drawShift(s.time(), canvas.getHeight()));
    }

    private int toRowStartX(JPanel exampleRow) {
        return SwingUtilities.convertPoint(exampleRow, 0, 0, canvas).x;
    }
}
