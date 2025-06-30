#!/bin/bash

# Healthcare Management System - Database Migration Script
# Migrates data from SQL Server to Heroku PostgreSQL

echo "🔄 Healthcare Management System - Database Migration"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if Heroku CLI is installed
if ! command -v heroku &> /dev/null; then
    print_error "Heroku CLI is not installed. Please install it first."
    echo "Download from: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

# Check if psql is installed
if ! command -v psql &> /dev/null; then
    print_error "PostgreSQL client (psql) is not installed."
    echo "Install with: sudo apt-get install postgresql-client"
    exit 1
fi

# Get Heroku app name
read -p "Enter your Heroku app name: " APP_NAME
if [ -z "$APP_NAME" ]; then
    print_error "App name is required!"
    exit 1
fi

print_status "Checking Heroku app: $APP_NAME"

# Check if app exists
if ! heroku apps:info $APP_NAME &> /dev/null; then
    print_error "Heroku app '$APP_NAME' not found!"
    print_warning "Create the app first with: heroku create $APP_NAME"
    exit 1
fi

# Get database URL
print_status "Getting database URL from Heroku..."
DATABASE_URL=$(heroku config:get DATABASE_URL --app $APP_NAME)

if [ -z "$DATABASE_URL" ]; then
    print_error "DATABASE_URL not found!"
    print_warning "Add PostgreSQL addon with: heroku addons:create heroku-postgresql:mini --app $APP_NAME"
    exit 1
fi

print_success "Database URL found"

# Create database schema
print_status "Creating database schema..."
if psql "$DATABASE_URL" -f database/postgresql-schema.sql; then
    print_success "Database schema created successfully!"
else
    print_error "Failed to create database schema!"
    exit 1
fi

# Optional: Export data from SQL Server (if you have existing data)
print_warning "Data migration from SQL Server to PostgreSQL requires manual steps:"
echo ""
echo "1. Export data from SQL Server:"
echo "   - Use SQL Server Management Studio"
echo "   - Export data as INSERT statements"
echo "   - Convert SQL Server syntax to PostgreSQL"
echo ""
echo "2. Data type conversions needed:"
echo "   - IDENTITY → SERIAL"
echo "   - NVARCHAR → VARCHAR"
echo "   - DATETIME → TIMESTAMP"
echo "   - BIT → BOOLEAN"
echo "   - UNIQUEIDENTIFIER → UUID"
echo ""
echo "3. Import to PostgreSQL:"
echo "   psql \"\$DATABASE_URL\" -f your-data.sql"
echo ""

# Create sample data (optional)
read -p "Do you want to insert sample data? (y/n): " INSERT_SAMPLE
if [[ $INSERT_SAMPLE =~ ^[Yy]$ ]]; then
    print_status "Inserting sample data..."
    
    # Create sample data file
    cat > temp_sample_data.sql << 'EOF'
-- Sample data for testing
INSERT INTO account_staff (email, password, full_name, role, department, specialization) VALUES
('doctor1@healthcare.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'Dr. John Smith', 'doctor', 'Internal Medicine', 'Cardiology'),
('doctor2@healthcare.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'Dr. Sarah Johnson', 'doctor', 'Pediatrics', 'General Pediatrics'),
('nurse1@healthcare.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'Nurse Mary Wilson', 'nurse', 'Emergency', NULL)
ON CONFLICT (email) DO NOTHING;

INSERT INTO account_patient (email, password, full_name, phone, gender, date_of_birth) VALUES
('patient1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'John Doe', '0123456789', 'male', '1990-01-15'),
('patient2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'Jane Smith', '0987654321', 'female', '1985-05-20')
ON CONFLICT (email) DO NOTHING;

INSERT INTO account_pharmacist (email, password, full_name, pharmacy_name, phone) VALUES
('pharmacist1@pharmacy.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lbdxOoRHebxny6jJ2', 'Pharmacist Alice Brown', 'City Pharmacy', '0111222333')
ON CONFLICT (email) DO NOTHING;
EOF

    if psql "$DATABASE_URL" -f temp_sample_data.sql; then
        print_success "Sample data inserted successfully!"
    else
        print_warning "Failed to insert sample data (may already exist)"
    fi
    
    # Clean up
    rm temp_sample_data.sql
fi

# Test database connection
print_status "Testing database connection..."
if psql "$DATABASE_URL" -c "SELECT COUNT(*) FROM account_staff;" > /dev/null 2>&1; then
    print_success "Database connection test passed!"
else
    print_error "Database connection test failed!"
    exit 1
fi

# Show database info
print_status "Database migration completed!"
echo ""
echo "📊 Database Information:"
heroku pg:info --app $APP_NAME
echo ""
echo "🔗 Database URL: $DATABASE_URL"
echo ""
echo "📝 Next steps:"
echo "1. Update your application code to use PostgreSQL"
echo "2. Set environment variables: heroku config:set ENV=production --app $APP_NAME"
echo "3. Deploy your application: git push heroku main"
echo "4. Test your application: heroku open --app $APP_NAME"
echo ""
print_success "Migration script completed successfully!"

# Optional: Open database in browser
read -p "Do you want to view database in browser? (y/n): " OPEN_DB
if [[ $OPEN_DB =~ ^[Yy]$ ]]; then
    heroku addons:open heroku-postgresql --app $APP_NAME
fi 