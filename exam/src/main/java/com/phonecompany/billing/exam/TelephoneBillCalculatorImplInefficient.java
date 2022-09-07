package com.phonecompany.billing.exam;

import com.phonecompany.billing.TelephoneBillCalculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TelephoneBillCalculatorImplInefficient implements TelephoneBillCalculator {
    public static final double DAY_RATE = 1.0;
    public static final double NIGHT_RATE = 0.5;
    public static final double LONG_RATE = 0.2;
    public static final LocalTime DAY_START = LocalTime.of(8, 0);
    public static final LocalTime NIGHT_START = LocalTime.of(16, 0);

    @Override
    public BigDecimal calculate(final String phoneLog) {
        return phoneLog.lines()
                .map(e -> new PhoneLogRecord(e.split(",")[0], LocalDateTime.parse(e.split(",")[1]), LocalDateTime.parse(e.split(",")[2])))
                .collect(Collectors.groupingBy(PhoneLogRecord::phoneNo))
                .entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().size()))
                .sorted(Collections.reverseOrder())
                .skip(1)
                .flatMap(e -> e.getValue().stream().map(r -> TelephoneBillCalculatorImplInefficient.costOfSingleCall(r.start(), r.end())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private static BigDecimal costOfSingleCall(final LocalDateTime start, final LocalDateTime end) {
        final BigDecimal res = BigDecimal.ZERO;
        final LocalTime startTime = start.toLocalTime();
        final LocalTime endTime = end.toLocalTime();

        if (DAY_START.isAfter(startTime)) {
            res.add(BigDecimal.valueOf(Math.min(5*60, Math.min(
                    Duration.between(start, end).get(ChronoUnit.SECONDS) * NIGHT_RATE / 60,
                    Duration.between(start, DAY_START).get(ChronoUnit.SECONDS)) * NIGHT_RATE / 60
            )));
            if (DAY_START.compareTo(endTime) >= 0 && startTime.plusMinutes(5).compareTo(DAY_START) >= 0)
                res.add(BigDecimal.valueOf(
                        Math.min(5*60,
                        Duration.between(DAY_START, end).get(ChronoUnit.SECONDS)) * DAY_RATE / 60
                ));
        }

        //TODO complete day rate

        if (Duration.between(start, end).get(ChronoUnit.SECONDS) > 5*60)
            res.add(BigDecimal.valueOf((Duration.between(start, end).get(ChronoUnit.SECONDS) - 5*60) * LONG_RATE));

        return res;
    }
}
