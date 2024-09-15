package work.rothe.tav.ui.canvas.rows;

import lombok.Getter;
import lombok.val;
import work.rothe.tav.model.Document;
import work.rothe.tav.model.Member;
import work.rothe.tav.ui.canvas.mouse.MemberRowMouseListener;
import work.rothe.tav.ui.canvas.util.Boundaries;
import work.rothe.tav.ui.canvas.util.CanvasCalculator;
import work.rothe.tav.ui.canvas.painters.CrossHatch;
import work.rothe.tav.ui.canvas.util.Palette;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Comparator;
import java.util.List;

@Getter
public class MemberRow extends AbstractZoneRow {
    private static final Color LUNCH_LINE = new Color(255, 0, 255, 25);
    private final Document document;
    private final Member member;
    private int dragOffsetHours;

    public MemberRow(Document document, Member member, CanvasCalculator calculator, Palette palette) {
        super(calculator, member.zone(), palette);
        setOpaque(false);
        this.document = document;
        this.member = member;

        MemberRowMouseListener.register(document, this, calculator);
    }

    public void setDragOffsetHours(int hours) {
        this.dragOffsetHours = hours;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        val g2d = (Graphics2D) g;

        val normalBoundaries = normalBoundaries();

        paintShapes(g2d, toShapes(normalBoundaries), toShapes(lunchBoundaries()));

        drawText(g2d, normalBoundaries);
    }

    private void drawText(Graphics2D g2d, List<Boundaries> normalBoundaries) {
        // TODO: render the text in pieces across the schedule, if necessary
        normalBoundaries.stream()
                .max(Comparator.comparing(Boundaries::width))
                .ifPresent(b -> drawText(g2d, b));
    }

    private void drawText(Graphics2D g2d, Boundaries boundaries) {
        g2d.setColor(getTextColor());
        drawCentered(g2d, getDisplayString(), boundaries.left(), boundaries.width());
    }

    private void paintShapes(Graphics2D g2d, List<Shape> normal, List<Shape> lunch) {
        normal.forEach(s -> fill(g2d, s, getFillColor()));
        lunch.forEach(s -> drawLunch(g2d, s));
        normal.forEach(s -> draw(g2d, s, getLineColor()));
    }

    private void fill(Graphics2D g, Shape s, Color fill) {
        g.setColor(fill);
        g.fill(s);
    }

    private void draw(Graphics2D g, Shape s, Color line) {
        g.setColor(line);
        g.draw(s);
    }

    private void drawLunch(Graphics2D g, Shape shape) {
        g.setColor(LUNCH_LINE);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.draw(shape);
        g2.setClip(shape);

        CrossHatch.draw(g2, shape);

        g2.dispose();
    }

    private List<Boundaries> normalBoundaries() {
        return getCalculator().toCenterBoundaries(member.normal().addHours(dragOffsetHours));
    }

    private List<Boundaries> lunchBoundaries() {
        return getCalculator().toCenterBoundaries(member.lunch().addHours(dragOffsetHours));
    }

    private List<Shape> toShapes(List<Boundaries> boundaries) {
        return boundaries.stream().map(this::toShape).toList();
    }

    private Shape toShape(Boundaries boundaries) {
        double x = boundaries.left() + 1;
        double y = 1;
        double width = boundaries.width() - 2;
        double height = getHeight() - 2;
        double arc = getHeight() / 3.0;
        return new RoundRectangle2D.Double(x, y, width, height, arc, arc);
    }

    @Override
    protected int getRowMinX() {
        return timeToColumnCenter(member.normal().left());
    }

    @Override
    protected int getRowMaxX() {
        return timeToColumnCenter(member.normal().right());
    }

    private String getDisplayString() {
        return String.format("%s (%s / %s)", member.name(), member.role(), member.location());
    }
}