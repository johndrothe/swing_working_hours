package org.rothe.john.team_schedule.ui.canvas;

import org.rothe.john.team_schedule.model.Member;
import org.rothe.john.team_schedule.util.Palette;
import lombok.Getter;
import lombok.val;

import java.awt.Graphics;
import java.awt.Graphics2D;

@Getter
public class MemberRenderer extends ZonedRenderer {
    private final Member member;

    public MemberRenderer(Member member, Palette palette) {
        super(member.zoneId(), palette);
        setOpaque(false);
        this.member = member;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        val g2d = (Graphics2D) g;

        g2d.setColor(getTextColor());
        drawCentered(getDisplayString(), getRendererLeftLocation(), getRendererDrawWidth(), g2d);
    }

    @Override
    protected int getRendererLeftLocation() {
        System.err.printf("getRendererLeftLocation %s - %s: %s (%d) -> %d\n",
                member.name(), getZoneAbbrev(), member.availability().start(),
                getUtcOffset(), timeToColumnCenter(member.availability().start()));
        return timeToColumnCenter(member.availability().start());
    }

    @Override
    protected int getRendererRightLocation() {
        System.err.printf("getRendererRightLocation %s - %s: %s (%d) -> %d\n",
                member.name(), getZoneAbbrev(), member.availability().end(),
                getUtcOffset(), timeToColumnCenter(member.availability().end()));
        return timeToColumnCenter(member.availability().end());
    }

    private String getDisplayString() {
        return String.format("%s (%s / %s)", member.name(), member.position(), member.location());
    }
}