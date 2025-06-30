#!/usr/bin/env pwsh
# Quick Deploy Script - No Maven Required
# Healthcare Management System

Write-Host "🚀 Quick Deploy to Heroku (No Maven)" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

# Check Heroku CLI
if (-not (Get-Command "heroku" -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Heroku CLI is not installed." -ForegroundColor Red
    Write-Host "Download from: https://cli.heroku.com/" -ForegroundColor Yellow
    exit 1
}

# Generate unique app name
$appName = "healthcare-$(Get-Random -Minimum 1000 -Maximum 9999)"
Write-Host "App name: $appName" -ForegroundColor Cyan

try {
    Write-Host "🔧 Logging into Heroku..." -ForegroundColor Yellow
    heroku auth:login

    Write-Host "📱 Creating Heroku app..." -ForegroundColor Yellow
    heroku create $appName

    Write-Host "🗄️ Adding PostgreSQL database..." -ForegroundColor Yellow
    heroku addons:create heroku-postgresql:mini --app $appName

    Write-Host "⚙️ Setting environment variables..." -ForegroundColor Yellow
    heroku config:set ENV=production --app $appName
    heroku config:set TZ=Asia/Ho_Chi_Minh --app $appName

    Write-Host "🚀 Deploying code..." -ForegroundColor Yellow
    git push heroku HEAD:main

    Write-Host "🗄️ Setting up database schema..." -ForegroundColor Yellow
    heroku pg:psql --app $appName -f database/postgresql-schema.sql

    Write-Host "" -ForegroundColor Green
    Write-Host "🎉 DEPLOYMENT SUCCESSFUL!" -ForegroundColor Green
    Write-Host "=========================" -ForegroundColor Green
    Write-Host "🌐 Website: https://$appName.herokuapp.com" -ForegroundColor Cyan
    Write-Host "👤 Admin login: admin / 123456" -ForegroundColor Yellow
    Write-Host "📊 Database: PostgreSQL (Heroku)" -ForegroundColor Yellow
    Write-Host "" -ForegroundColor Green
    
    # Open website
    Write-Host "🔗 Opening website..." -ForegroundColor Yellow
    Start-Process "https://$appName.herokuapp.com"

} catch {
    Write-Host "[ERROR] Deployment failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} 