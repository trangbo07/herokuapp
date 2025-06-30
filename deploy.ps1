# Healthcare Management System - Deploy Script for Windows
# PowerShell script for building and deploying the application

Write-Host "🚀 Healthcare Management System - Deploy Script" -ForegroundColor Cyan
Write-Host "==============================================`n" -ForegroundColor Cyan

# Function to print colored output
function Write-Info($message) {
    Write-Host "[INFO] $message" -ForegroundColor Blue
}

function Write-Success($message) {
    Write-Host "[SUCCESS] $message" -ForegroundColor Green
}

function Write-Error($message) {
    Write-Host "[ERROR] $message" -ForegroundColor Red
}

function Write-Warning($message) {
    Write-Host "[WARNING] $message" -ForegroundColor Yellow
}

# Check if Maven is installed
try {
    $mvnVersion = mvn -version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Maven not found"
    }
    Write-Success "Maven is installed"
} catch {
    Write-Error "Maven is not installed. Please install Maven first."
    Write-Host "Download from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Check Java version
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString().Split('"')[1] }
    $majorVersion = $javaVersion.Split('.')[0]
    
    if ($majorVersion -eq "17") {
        Write-Success "Java 17 is installed"
    } else {
        Write-Warning "Java 17 is recommended. Current version: $javaVersion"
    }
} catch {
    Write-Error "Java is not installed or not in PATH"
    exit 1
}

Write-Info "Cleaning previous builds..."
mvn clean | Out-Null

Write-Info "Building WAR file..."
$buildResult = mvn package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Success "Build completed successfully!"
    
    if (Test-Path "target\SWP391-1.0-SNAPSHOT.war") {
        Write-Success "WAR file created: target\SWP391-1.0-SNAPSHOT.war"
        
        # Show deployment options
        Write-Host "`n🌐 Deployment Options:" -ForegroundColor Cyan
        Write-Host "1. Local Testing: Run 'docker-compose up' (if Docker is available)"
        Write-Host "2. Heroku: Run 'git push heroku main' (if Heroku remote is configured)"
        Write-Host "3. AWS: Run 'eb deploy' (if EB CLI is configured)"
        Write-Host "4. Manual: Upload target\SWP391-1.0-SNAPSHOT.war to your Tomcat server"
        Write-Host ""
        
        # Ask user for deployment option
        Write-Host "Choose deployment option:"
        Write-Host "1) Docker (Local)"
        Write-Host "2) Heroku (Cloud)"
        Write-Host "3) Build only (Manual upload)"
        $choice = Read-Host "Enter choice (1-3)"
        
        switch ($choice) {
            "1" {
                try {
                    docker --version | Out-Null
                    Write-Info "Building Docker image..."
                    docker build -t healthcare-app .
                    
                    if ($LASTEXITCODE -eq 0) {
                        Write-Success "Docker image built successfully!"
                        Write-Info "Starting container on port 8080..."
                        docker run -d -p 8080:8080 --name healthcare-container healthcare-app
                        Write-Success "Application is running at http://localhost:8080"
                        Write-Host "To stop the container, run: docker stop healthcare-container" -ForegroundColor Yellow
                    } else {
                        Write-Error "Docker build failed!"
                    }
                } catch {
                    Write-Error "Docker is not installed or not in PATH."
                    Write-Host "Download from: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
                }
            }
            "2" {
                try {
                    heroku --version | Out-Null
                    Write-Info "Starting Heroku deployment process..."
                    
                    # Check if git repo is initialized
                    if (-not (Test-Path ".git")) {
                        Write-Info "Initializing Git repository..."
                        git init
                        git add .
                        git commit -m "Initial commit for Heroku deployment"
                    }
                    
                    # Get app name
                    $appName = Read-Host "Enter your Heroku app name (e.g., my-healthcare-app)"
                    if ([string]::IsNullOrEmpty($appName)) {
                        Write-Error "App name is required!"
                        return
                    }
                    
                    # Check if app exists, if not create it
                    $appExists = heroku apps:info $appName 2>$null
                    if ($LASTEXITCODE -ne 0) {
                        Write-Info "Creating Heroku app: $appName"
                        heroku create $appName
                        if ($LASTEXITCODE -ne 0) {
                            Write-Error "Failed to create Heroku app. Name might be taken."
                            return
                        }
                    } else {
                        Write-Warning "App '$appName' already exists, continuing with existing app"
                    }
                    
                    # Add PostgreSQL database
                    Write-Info "Adding PostgreSQL database..."
                    heroku addons:create heroku-postgresql:mini --app $appName 2>$null
                    
                    # Set environment variables
                    Write-Info "Setting environment variables..."
                    heroku config:set ENV=production --app $appName
                    heroku config:set JAVA_TOOL_OPTIONS="-Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8" --app $appName
                    heroku config:set VNPAY_TMN_CODE=DIMMABD6 --app $appName
                    heroku config:set VNPAY_HASH_SECRET=2BVC92IQL8S3WICDEHJ4CF15BM5GKDJA --app $appName
                    heroku config:set VNPAY_RETURN_URL="https://$appName.herokuapp.com/api/vnpay/return.html" --app $appName
                    
                    # Add Heroku remote
                    heroku git:remote -a $appName 2>$null
                    
                    # Deploy
                    Write-Info "Deploying to Heroku (this may take several minutes)..."
                    git add .
                    git commit -m "Deploy to Heroku" 2>$null
                    git push heroku main
                    
                    if ($LASTEXITCODE -eq 0) {
                        Write-Success "Heroku deployment completed!"
                        Write-Host "Your app is available at: https://$appName.herokuapp.com" -ForegroundColor Cyan
                        Write-Host "To setup database, see: HEROKU-DEPLOY.md" -ForegroundColor Yellow
                    } else {
                        Write-Error "Heroku deployment failed!"
                        Write-Host "Check logs with: heroku logs --tail --app $appName" -ForegroundColor Yellow
                    }
                } catch {
                    Write-Error "Heroku CLI is not installed."
                    Write-Host "Download from: https://devcenter.heroku.com/articles/heroku-cli" -ForegroundColor Yellow
                }
            }
            "3" {
                Write-Success "Build completed. Upload target\SWP391-1.0-SNAPSHOT.war to your server."
                Write-Host "File location: $PWD\target\SWP391-1.0-SNAPSHOT.war" -ForegroundColor Yellow
            }
            default {
                Write-Warning "Invalid choice. Build completed."
            }
        }
        
    } else {
        Write-Error "WAR file not found after build!"
        exit 1
    }
} else {
    Write-Error "Build failed! Please check the error messages above."
    exit 1
}

Write-Success "Deploy script completed!"
Write-Host "`nFor more deployment options, see deploy-guide.md" -ForegroundColor Cyan 