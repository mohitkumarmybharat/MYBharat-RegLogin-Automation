param(
    [string]$Suite = "testSuites/testng-youthclub.xml",
    [string]$Env = "prod",
    [string]$Browser = "chrome"
)

$suiteName = [System.IO.Path]::GetFileNameWithoutExtension($Suite)

Write-Host ""
Write-Host "MY Bharat - Local Test Run" -ForegroundColor Cyan
Write-Host "Suite: $Suite | Env: $Env | Browser: $Browser" -ForegroundColor Cyan
Write-Host ""

$env:env = $Env
$env:browser = $Browser

mvn clean test "-Denv=$Env" "-Dbrowser=$Browser" "-Dsurefire.suiteXmlFiles=$Suite" "-Dmaven.test.failure.ignore=true"

Write-Host ""
Write-Host "Sending email report..." -ForegroundColor Yellow

$scriptPath = $PSScriptRoot + "\send-report.ps1"
& $scriptPath -Suite $suiteName
