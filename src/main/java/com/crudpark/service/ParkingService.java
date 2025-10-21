// service/ParkingService.java
package com.crudpark.service;

import com.crudpark.dao.MembershipDAO;
import com.crudpark.dao.PaymentDAO;
import com.crudpark.dao.RateDAO;
import com.crudpark.dao.TicketDAO;
import com.crudpark.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class ParkingService {
    private TicketDAO ticketDAO = new TicketDAO();
    private MembershipDAO membershipDAO = new MembershipDAO();
    private RateDAO rateDAO = new RateDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    public Ticket registerEntry(String plate, Operator operator) throws SQLException {
        // Check for active ticket
        Optional<Ticket> activeTicket = ticketDAO.findActiveByPlate(plate);
        if (activeTicket.isPresent()) {
            throw new IllegalStateException("Vehicle already has an active ticket");
        }

        // Check membership
        Optional<Membership> membership = membershipDAO.findValidByPlate(plate);
        
        Ticket ticket = new Ticket();
        ticket.setPlate(plate.toUpperCase());
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setTicketType(membership.isPresent() ? "MONTHLY" : "GUEST");
        ticket.setOperatorId(operator.getId());
        ticket.setOperatorName(operator.getName());
        ticket.setActive(true);

        return ticketDAO.create(ticket);
    }

    public ExitResult processExit(String plate, Operator operator) throws SQLException {
        Optional<Ticket> ticketOpt = ticketDAO.findActiveByPlate(plate);
        if (!ticketOpt.isPresent()) {
            throw new IllegalStateException("No active ticket found for this plate");
        }

        Ticket ticket = ticketOpt.get();
        LocalDateTime exitTime = LocalDateTime.now();
        
        Duration duration = Duration.between(ticket.getEntryTime(), exitTime);
        long totalMinutes = duration.toMinutes();

        ExitResult result = new ExitResult();
        result.setTicket(ticket);
        result.setExitTime(exitTime);
        result.setTotalMinutes(totalMinutes);

        if ("MONTHLY".equals(ticket.getTicketType())) {
            result.setAmountToPay(BigDecimal.ZERO);
            result.setPaymentRequired(false);
        } else {
            BigDecimal amount = calculateFee(totalMinutes);
            result.setAmountToPay(amount);
            result.setPaymentRequired(amount.compareTo(BigDecimal.ZERO) > 0);
        }

        // Update ticket exit time
        ticketDAO.updateExit(ticket.getId(), exitTime);

        return result;
    }

    public Payment registerPayment(int ticketId, BigDecimal amount, String paymentMethod, int operatorId) throws SQLException {
        Payment payment = new Payment();
        payment.setTicketId(ticketId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setOperatorId(operatorId);

        return paymentDAO.create(payment);
    }

    private BigDecimal calculateFee(long totalMinutes) throws SQLException {
        Optional<Rate> rateOpt = rateDAO.getActiveRate();
        if (!rateOpt.isPresent()) {
            throw new IllegalStateException("No active rate configured");
        }

        Rate rate = rateOpt.get();

        // Grace period
        if (totalMinutes <= rate.getGraceMinutes()) {
            return BigDecimal.ZERO;
        }

        // Calculate hours
        BigDecimal hours = BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);

        // Base rate for first hour + fraction rate for additional hours
        BigDecimal amount;
        if (hours.compareTo(BigDecimal.ONE) <= 0) {
            amount = rate.getBaseRate();
        } else {
            BigDecimal additionalHours = hours.subtract(BigDecimal.ONE);
            amount = rate.getBaseRate().add(
                additionalHours.multiply(rate.getFractionRate())
            );
        }

        // Apply daily cap
        if (rate.getDailyCap() != null && amount.compareTo(rate.getDailyCap()) > 0) {
            amount = rate.getDailyCap();
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    // Inner class for exit results
    public static class ExitResult {
        private Ticket ticket;
        private LocalDateTime exitTime;
        private long totalMinutes;
        private BigDecimal amountToPay;
        private boolean paymentRequired;

        public Ticket getTicket() { return ticket; }
        public void setTicket(Ticket ticket) { this.ticket = ticket; }
        
        public LocalDateTime getExitTime() { return exitTime; }
        public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
        
        public long getTotalMinutes() { return totalMinutes; }
        public void setTotalMinutes(long totalMinutes) { this.totalMinutes = totalMinutes; }
        
        public BigDecimal getAmountToPay() { return amountToPay; }
        public void setAmountToPay(BigDecimal amountToPay) { this.amountToPay = amountToPay; }
        
        public boolean isPaymentRequired() { return paymentRequired; }
        public void setPaymentRequired(boolean paymentRequired) { this.paymentRequired = paymentRequired; }
    }
}