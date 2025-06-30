#!/bin/bash

echo "🚀 Healthcare Management System - Deploy Script"
echo "=============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java 17 is installed
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" != "17" ]; then
    print_warning "Java 17 is required. Current version: $java_version"
fi

print_status "Cleaning previous builds..."
mvn clean

print_status "Building WAR file..."
if mvn package -DskipTests; then
    print_success "Build completed successfully!"
    
    if [ -f "target/SWP391-1.0-SNAPSHOT.war" ]; then
        print_success "WAR file created: target/SWP391-1.0-SNAPSHOT.war"
        
        # Show deployment options
        echo ""
        echo "🌐 Deployment Options:"
        echo "1. Local Testing: Run 'docker-compose up' (if Docker is available)"
        echo "2. Heroku: Run 'git push heroku main' (if Heroku remote is configured)"
        echo "3. AWS: Run 'eb deploy' (if EB CLI is configured)"
        echo "4. Manual: Upload target/SWP391-1.0-SNAPSHOT.war to your Tomcat server"
        echo ""
        
        # Ask user for deployment option
        echo "Choose deployment option:"
        echo "1) Docker (Local)"
        echo "2) Heroku"
        echo "3) Build only (Manual upload)"
        read -p "Enter choice (1-3): " choice
        
        case $choice in
            1)
                if command -v docker &> /dev/null; then
                    print_status "Building Docker image..."
                    docker build -t healthcare-app .
                    print_success "Docker image built successfully!"
                    print_status "Starting container on port 8080..."
                    docker run -d -p 8080:8080 --name healthcare-container healthcare-app
                    print_success "Application is running at http://localhost:8080"
                else
                    print_error "Docker is not installed."
                fi
                ;;
            2)
                if command -v heroku &> /dev/null; then
                    print_status "Deploying to Heroku..."
                    git add .
                    git commit -m "Deploy to Heroku"
                    git push heroku main
                else
                    print_error "Heroku CLI is not installed."
                fi
                ;;
            3)
                print_success "Build completed. Upload target/SWP391-1.0-SNAPSHOT.war to your server."
                ;;
            *)
                print_warning "Invalid choice. Build completed."
                ;;
        esac
        
    else
        print_error "WAR file not found after build!"
        exit 1
    fi
else
    print_error "Build failed!"
    exit 1
fi

print_success "Deploy script completed!" 