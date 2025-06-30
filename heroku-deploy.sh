#!/bin/bash

# Healthcare Management System - Automated Heroku Deployment Script
# This script handles the complete deployment process to Heroku

echo "🚀 Healthcare Management System - Heroku Deployment"
echo "================================================="

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

# Check prerequisites
print_status "Checking prerequisites..."

# Check if Heroku CLI is installed
if ! command -v heroku &> /dev/null; then
    print_error "Heroku CLI is not installed!"
    echo "Download from: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed!"
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check if Git is installed
if ! command -v git &> /dev/null; then
    print_error "Git is not installed!"
    echo "Download from: https://git-scm.com/downloads"
    exit 1
fi

print_success "All prerequisites are installed"

# Login to Heroku
print_status "Checking Heroku authentication..."
if ! heroku auth:whoami &> /dev/null; then
    print_warning "Not logged in to Heroku"
    heroku login
fi

print_success "Authenticated with Heroku"

# Get app name
echo ""
print_status "App Configuration"
read -p "Enter your Heroku app name (e.g., my-healthcare-app): " APP_NAME

if [ -z "$APP_NAME" ]; then
    print_error "App name is required!"
    exit 1
fi

# Check if app already exists
if heroku apps:info $APP_NAME &> /dev/null; then
    print_warning "App '$APP_NAME' already exists"
    read -p "Do you want to continue with existing app? (y/n): " CONTINUE
    if [[ ! $CONTINUE =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    # Create new app
    print_status "Creating Heroku app: $APP_NAME"
    if heroku create $APP_NAME; then
        print_success "App created successfully!"
    else
        print_error "Failed to create app. Maybe the name is already taken?"
        exit 1
    fi
fi

# Add PostgreSQL addon
print_status "Adding PostgreSQL database..."
if heroku addons:info heroku-postgresql --app $APP_NAME &> /dev/null; then
    print_warning "PostgreSQL addon already exists"
else
    heroku addons:create heroku-postgresql:mini --app $APP_NAME
    print_success "PostgreSQL database added"
fi

# Add other useful addons
print_status "Adding logging addon..."
if ! heroku addons:info papertrail --app $APP_NAME &> /dev/null; then
    heroku addons:create papertrail:choklad --app $APP_NAME || print_warning "Could not add papertrail addon"
fi

# Set environment variables
print_status "Setting environment variables..."

heroku config:set ENV=production --app $APP_NAME
heroku config:set JAVA_TOOL_OPTIONS="-Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8" --app $APP_NAME

# VnPay configuration
print_status "Configuring VnPay settings..."
heroku config:set VNPAY_TMN_CODE=DIMMABD6 --app $APP_NAME
heroku config:set VNPAY_HASH_SECRET=2BVC92IQL8S3WICDEHJ4CF15BM5GKDJA --app $APP_NAME
heroku config:set VNPAY_RETURN_URL=https://$APP_NAME.herokuapp.com/api/vnpay/return.html --app $APP_NAME

# Email configuration (optional)
echo ""
read -p "Do you want to configure email settings? (y/n): " CONFIG_EMAIL
if [[ $CONFIG_EMAIL =~ ^[Yy]$ ]]; then
    read -p "SMTP Host (e.g., smtp.gmail.com): " SMTP_HOST
    read -p "SMTP Port (e.g., 587): " SMTP_PORT
    read -p "SMTP Username: " SMTP_USERNAME
    read -s -p "SMTP Password: " SMTP_PASSWORD
    echo ""
    
    heroku config:set SMTP_HOST=$SMTP_HOST --app $APP_NAME
    heroku config:set SMTP_PORT=$SMTP_PORT --app $APP_NAME
    heroku config:set SMTP_USERNAME=$SMTP_USERNAME --app $APP_NAME
    heroku config:set SMTP_PASSWORD=$SMTP_PASSWORD --app $APP_NAME
    
    print_success "Email configuration set"
fi

# Build application
print_status "Building application..."
if mvn clean package -DskipTests; then
    print_success "Build completed successfully!"
else
    print_error "Build failed!"
    exit 1
fi

# Initialize git repository if needed
if [ ! -d ".git" ]; then
    print_status "Initializing Git repository..."
    git init
    git add .
    git commit -m "Initial commit for Heroku deployment"
fi

# Add Heroku remote
print_status "Adding Heroku remote..."
if git remote get-url heroku &> /dev/null; then
    print_warning "Heroku remote already exists"
else
    heroku git:remote -a $APP_NAME
    print_success "Heroku remote added"
fi

# Deploy to Heroku
print_status "Deploying to Heroku..."
echo "This may take several minutes..."

if git push heroku main; then
    print_success "Deployment completed successfully!"
else
    print_error "Deployment failed!"
    print_warning "Check the logs with: heroku logs --tail --app $APP_NAME"
    exit 1
fi

# Setup database
print_status "Setting up database..."
if [ -f "database/postgresql-schema.sql" ]; then
    DATABASE_URL=$(heroku config:get DATABASE_URL --app $APP_NAME)
    if command -v psql &> /dev/null; then
        psql "$DATABASE_URL" -f database/postgresql-schema.sql
        print_success "Database schema created"
    else
        print_warning "psql not found. Database schema not created automatically."
        echo "Run manually: heroku pg:psql --app $APP_NAME < database/postgresql-schema.sql"
    fi
else
    print_warning "Database schema file not found"
fi

# Check application health
print_status "Checking application health..."
sleep 10 # Wait for app to start

APP_URL="https://$APP_NAME.herokuapp.com"
if curl -f -s "$APP_URL" > /dev/null; then
    print_success "Application is running!"
else
    print_warning "Application may not be responding yet"
fi

# Show deployment summary
echo ""
echo "🎉 Deployment Summary"
echo "===================="
echo "App Name: $APP_NAME"
echo "App URL: $APP_URL"
echo "Database: PostgreSQL (Heroku)"
echo "Environment: Production"
echo ""
echo "📝 Useful Commands:"
echo "View logs: heroku logs --tail --app $APP_NAME"
echo "Open app: heroku open --app $APP_NAME"
echo "Database console: heroku pg:psql --app $APP_NAME"
echo "Restart app: heroku restart --app $APP_NAME"
echo "View config: heroku config --app $APP_NAME"
echo ""
echo "🔧 Next Steps:"
echo "1. Test your application: $APP_URL"
echo "2. Configure custom domain (if needed)"
echo "3. Set up monitoring and backups"
echo "4. Update DNS settings (if using custom domain)"
echo ""

# Open application in browser
read -p "Do you want to open the application in browser? (y/n): " OPEN_APP
if [[ $OPEN_APP =~ ^[Yy]$ ]]; then
    heroku open --app $APP_NAME
fi

# Show logs
read -p "Do you want to view application logs? (y/n): " SHOW_LOGS
if [[ $SHOW_LOGS =~ ^[Yy]$ ]]; then
    echo "Press Ctrl+C to exit logs"
    heroku logs --tail --app $APP_NAME
fi

print_success "Heroku deployment script completed!"
echo "Your healthcare management system is now live at: $APP_URL" 