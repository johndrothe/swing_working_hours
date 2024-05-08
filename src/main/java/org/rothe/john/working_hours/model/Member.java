package org.rothe.john.working_hours.model;

import lombok.With;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

@With
public record Member(String name, String role, String location, Zone zone, Availability availability) {
    public Member(String name, String role, String location, Zone zone) {
        this(name, role, location, zone, Availability.standard());
    }

    public String toCsv() {
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s",
                name(), role(), location(), zone().getId(),
                availability().normalStart(),
                availability().normalEnd(),
                availability().lunchStart(),
                availability().lunchEnd());
    }

    public static Member fromCsv(String[] values) {
        return new Member(values[0],
                values[1],
                values[2],
                Zone.fromCsv(values[3]),
                new Availability(
                        Time.parse(values[4]),
                        Time.parse(values[5]),
                        Time.parse(values[6]),
                        Time.parse(values[7])
                )
        );
    }

    public int getNormalStartMinutesUtc() {
        return zone.toMinutesUtc(availability.normalStart());
    }

    public int getNormalEndMinutesUtc() {
        return zone.toMinutesUtc(availability.normalEnd());
    }

    public static String toNameList(Collection<Member> members) {
        return members.stream()
                .map(Member::name)
                .sorted()
                .collect(joining(", "));
    }

    public static Comparator<Member> offsetNameCompartor() {
        final Comparator<Member> z = comparing(m -> m.zone().getOffsetHours());
        final Comparator<Member> n = comparing(Member::name);
        return z.thenComparing(n);
    }
}
