package com.phonecompany.billing.exam;

import java.time.LocalDateTime;

/**
 * Representation of a single phone call from the log.
 * @param phoneNo called telephone number
 * @param start start date+time of the phone call
 * @param end date+time when the phone call terminated
 */
public record PhoneLogRecord(String phoneNo, LocalDateTime start, LocalDateTime end) {
}
