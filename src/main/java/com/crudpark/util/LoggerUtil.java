// util/LoggerUtil.java
package com.crudpark.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    public static void logEntry(Logger logger, int ticketId, String plate, String type, String operator) {
        logger.info("ENTRY - Ticket: {} | Plate: {} | Type: {} | Operator: {}", 
                    ticketId, plate, type, operator);
    }
    
    public static void logExit(Logger logger, int ticketId, String plate, long minutes, double amount) {
        logger.info("EXIT - Ticket: {} | Plate: {} | Duration: {}min | Amount: ${}", 
                    ticketId, plate, minutes, amount);
    }
    
    public static void logPayment(Logger logger, int ticketId, double amount, String method, String operator) {
        logger.info("PAYMENT - Ticket: {} | Amount: ${} | Method: {} | Operator: {}", 
                    ticketId, amount, method, operator);
    }
    
    public static void logError(Logger logger, String operation, String details, Exception e) {
        logger.error("ERROR in {} - {} : {}", operation, details, e.getMessage(), e);
    }
}