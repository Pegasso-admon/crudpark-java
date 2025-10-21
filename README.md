# CrudPark - Java Desktop Application

## Project Objective
Desktop operational application for managing vehicle entries and exits in a parking system.

## Features
- ✅ Operator authentication
- ✅ Vehicle entry registration
- ✅ Vehicle exit processing with automatic fee calculation
- ✅ Membership validation
- ✅ Payment processing (Cash, Card, Transfer)
- ✅ QR code generation for tickets
- ✅ Ticket printing (thermal printer support)
- ✅ Grace period handling (30 minutes default)
- ✅ Real-time logging

## Requirements
- **Java Development Kit (JDK)** 23 or higher
- **Apache Maven** 3.6+
- **MySQL Database** 5.7+ or 8.0+
- **Thermal Printer** (optional, for ticket printing)

## Project Structure
```
crudpark-java/
├── pom.xml
├── README.md
├── database_schema.sql
└── src/
    ├── main/
    │   ├── java/com/crudpark/
    │   │   ├── MainApp.java
    │   │   ├── dao/
    │   │   │   ├── MembershipDAO.java
    │   │   │   ├── OperatorDAO.java
    │   │   │   ├── PaymentDAO.java
    │   │   │   ├── RateDAO.java
    │   │   │   └── TicketDAO.java
    │   │   ├── model/
    │   │   │   ├── Membership.java
    │   │   │   ├── Operator.java
    │   │   │   ├── Payment.java
    │   │   │   ├── Rate.java
    │   │   │   └── Ticket.java
    │   │   ├── service/
    │   │   │   ├── OperatorService.java
    │   │   │   └── ParkingService.java
    │   │   ├── ui/
    │   │   │   ├── LoginDialog.java
    │   │   │   ├── MainFrame.java
    │   │   │   ├── VehicleEntryPanel.java
    │   │   │   └── VehicleExitPanel.java
    │   │   └── util/
    │   │       ├── DbConnection.java
    │   │       ├── QRCodeGenerator.java
    │   │       └── TicketPrinter.java
    │   └── resources/
    │       └── db.properties
    └── test/ (optional)
```

## Installation and Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd crudpark-java
```

### 2. Configure Database
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE
db.username=YOUR_USERNAME
db.password=YOUR_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

### 3. Create Database Tables
Run the provided SQL script:
```bash
mysql -h YOUR_HOST -P YOUR_PORT -u YOUR_USERNAME -p YOUR_DATABASE < database_schema.sql
```

Or execute the `database_schema.sql` file using your preferred MySQL client.

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.crudpark.MainApp"
```

## Default Credentials
- **Username**: `admin`
- **Password**: `admin123`

> ⚠️ **Security Note**: Change the default password in production environments. In a real application, passwords should be hashed using bcrypt or similar.

## Usage

### Vehicle Entry
1. Login with operator credentials
2. Navigate to "Vehicle Entry" tab
3. Enter vehicle plate number
4. Click "Register Entry"
5. System automatically detects if vehicle has valid membership
6. Ticket is generated with QR code

### Vehicle Exit
1. Navigate to "Vehicle Exit" tab
2. Enter vehicle plate number
3. Click "Process Exit"
4. System calculates fee based on:
   - Time stayed
   - Current rate configuration
   - Grace period (30 minutes default)
   - Membership status
5. If payment required, select payment method
6. Exit is registered

## Business Rules
- **Grace Period**: First 30 minutes are free
- **Single Active Ticket**: Only one active ticket per plate allowed
- **Valid Membership**: Automatic entry/exit without charges
- **Rate Calculation**: 
  - Base rate for first hour
  - Fraction rate for additional time
  - Daily cap applies if exceeded
- **Payment Methods**: Cash, Card, Transfer

## Database Schema
The application uses 5 main tables:
- `operators` - System users
- `memberships` - Monthly parking passes
- `tickets` - Entry/exit records
- `payments` - Payment transactions
- `rates` - Pricing configuration

## Dependencies
- **MySQL Connector/J** 8.0.33 - Database connectivity
- **ZXing** 3.5.3 - QR code generation
- **Log4j2** 2.23.1 - Logging (optional)
- **JUnit Jupiter** 5.10.0 - Testing

## Troubleshooting

### Cannot Connect to Database
- Verify database credentials in `db.properties`
- Ensure MySQL server is running
- Check firewall rules for database port

### Ticket Printing Issues
- Ensure printer drivers are installed
- Check printer connection
- If printing fails, ticket preview is saved as PNG image
- Verify thermal printer paper size (80mm recommended)

### Class Not Found Exception
- Run `mvn clean install` to download dependencies
- Verify Maven settings and internet connection

## Development Team
- [Team Member 1 - Java Developer]
- [Team Member 2 - C# Developer]
- [Team Member 3 - C# Developer]

**Team Registration**: [https://teams.crudzaso.com](https://teams.crudzaso.com)

## Future Enhancements
- [ ] Password hashing (BCrypt)
- [ ] Operator shift management
- [ ] Advanced reporting
- [ ] Barcode scanner integration
- [ ] Email notifications
- [ ] Multi-language support

## License
Educational project for CrudPark challenge - Crudzaso

## Support
For issues or questions, contact your team members or instructors.