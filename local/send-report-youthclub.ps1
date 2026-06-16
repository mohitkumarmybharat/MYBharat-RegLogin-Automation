param(
    [string]$Suite = "Youth Club"
)

$configFile = Join-Path $PSScriptRoot "email-config.properties"
if (-not (Test-Path $configFile)) {
    Write-Host "ERROR: email-config.properties not found!" -ForegroundColor Red
    exit 1
}

$config = @{}
Get-Content $configFile | ForEach-Object {
    if ($_ -match '^([^#=]+)=(.+)$') {
        $config[$matches[1].Trim()] = $matches[2].Trim()
    }
}

if ($config['smtp.password'] -eq 'YOUR_APP_PASSWORD_HERE') {
    Write-Host "ERROR: Set Gmail App Password in local/email-config.properties" -ForegroundColor Red
    exit 1
}

$reportFile = "target\surefire-reports\testng-results.xml"

function Get-TestStatus($methodName) {
    if (-not (Test-Path $reportFile)) { return "SKIP" }
    $content = Get-Content $reportFile -Raw
    if ($content -match "name=`"$methodName`"") {
        if ($content -match "name=`"$methodName`"[\s\S]*?status=`"FAIL`"") {
            return "FAIL"
        }
        return "PASS"
    }
    return "SKIP"
}

function Get-StatusIcon($status) {
    switch ($status) {
        "PASS" { return "<span style='color:#28a745;font-weight:bold;'>&#9989; PASS</span>" }
        "FAIL" { return "<span style='color:#dc3545;font-weight:bold;'>&#10060; FAIL</span>" }
        default { return "<span style='color:#ffc107;'>&#9889; SKIP</span>" }
    }
}

# Youth Club test statuses (2 test cases)
$s_createYouthClub = Get-TestStatus "step15_submit"
$s_approveYouthClub = Get-TestStatus "step19_superAdminApprove"

$allStatuses = @($s_createYouthClub, $s_approveYouthClub)
$total = $allStatuses.Count
$passed = ($allStatuses | Where-Object { $_ -eq "PASS" }).Count
$failed = ($allStatuses | Where-Object { $_ -eq "FAIL" }).Count
$skipped = ($allStatuses | Where-Object { $_ -eq "SKIP" }).Count

if ($failed -eq 0 -and $skipped -eq 0) {
    $status = "ALL TESTS PASSED"
    $emoji = "&#9989;"
    $color = "#28a745"
} elseif ($failed -gt 0) {
    $status = "SOME TESTS FAILED"
    $emoji = "&#10060;"
    $color = "#dc3545"
} else {
    $status = "TESTS SKIPPED"
    $emoji = "&#9888;"
    $color = "#ffc107"
}

$timestamp = Get-Date -Format "dd-MMM-yyyy hh:mm tt IST"
$envName = if ($env:env) { $env:env.ToUpper() } else { "PROD" }
$baseUrl = if ($envName -eq "PROD") { "https://mybharat.gov.in" } else { "https://yuva-beta.mybharats.in" }

$htmlBody = @"
<div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 700px; margin: 0 auto;">
  <div style="background: linear-gradient(135deg, #160363, #2d0a8f); padding: 25px; text-align: center; border-radius: 8px 8px 0 0;">
    <h1 style="color: #fff; margin: 0; font-size: 22px;">MY Bharat &#8212; QA Automation Report</h1>
    <p style="color: #c4b5fd; margin: 8px 0 0; font-size: 13px;">$envName Environment | Youth Club Module Test</p>
  </div>
  <div style="background: $color; padding: 14px; text-align: center;">
    <h2 style="color: #fff; margin: 0; font-size: 20px;">$emoji $status</h2>
  </div>
  <div style="padding: 25px; border: 1px solid #e0e0e0; border-top: none;">
    <h3 style="color: #160363; margin: 0 0 12px; border-bottom: 2px solid #160363; padding-bottom: 5px;">Execution Details</h3>
    <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
      <tr><td style="padding: 8px; font-weight: bold; width: 40%;">Execution Time:</td><td style="padding: 8px;">$timestamp</td></tr>
      <tr style="background:#f9f9f9;"><td style="padding: 8px; font-weight: bold;">Environment:</td><td style="padding: 8px;">$envName ($baseUrl)</td></tr>
      <tr><td style="padding: 8px; font-weight: bold;">Browser:</td><td style="padding: 8px;">Google Chrome (UI Mode)</td></tr>
      <tr style="background:#f9f9f9;"><td style="padding: 8px; font-weight: bold;">Module:</td><td style="padding: 8px;">Youth Club (Create + Approve)</td></tr>
      <tr><td style="padding: 8px; font-weight: bold;">Machine:</td><td style="padding: 8px;">$env:COMPUTERNAME (Local)</td></tr>
    </table>

    <h3 style="color: #160363; margin: 0 0 12px; border-bottom: 2px solid #160363; padding-bottom: 5px;">Test Results Summary</h3>
    <table style="width: 100%; border-collapse: collapse; text-align: center; margin-bottom: 20px;">
      <tr style="background: #160363; color: white;">
        <th style="padding: 10px;">Total Tests</th><th style="padding: 10px;">Passed</th><th style="padding: 10px;">Failed</th><th style="padding: 10px;">Skipped</th>
      </tr>
      <tr style="font-size: 20px; font-weight: bold;">
        <td style="padding: 12px; border: 1px solid #ddd;">$total</td>
        <td style="padding: 12px; border: 1px solid #ddd; color: #28a745;">$passed</td>
        <td style="padding: 12px; border: 1px solid #ddd; color: #dc3545;">$failed</td>
        <td style="padding: 12px; border: 1px solid #ddd; color: #ffc107;">$skipped</td>
      </tr>
    </table>

    <h3 style="color: #160363; margin: 0 0 12px; border-bottom: 2px solid #160363; padding-bottom: 5px;">Test Cases Executed</h3>
    <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px; font-size: 12px;">
      <tr style="background: #160363; color: white;">
        <th style="padding: 6px;">#</th>
        <th style="padding: 6px; text-align: left;">Module</th>
        <th style="padding: 6px; text-align: left;">Test Case</th>
        <th style="padding: 6px; text-align: left;">Description</th>
        <th style="padding: 6px;">Device</th>
        <th style="padding: 6px;">Status</th>
        <th style="padding: 6px; text-align: left;">Team Lead</th>
      </tr>
      <tr style="background: #e8f5e9;">
        <td style="padding: 6px; border: 1px solid #eee; text-align: center;">1</td>
        <td style="padding: 6px; border: 1px solid #eee;"><b>Create Youth Club</b></td>
        <td style="padding: 6px; border: 1px solid #eee;">createYouthClub</td>
        <td style="padding: 6px; border: 1px solid #eee;">Register 6 members + Create Youth Club + Add members with OTP + Submit</td>
        <td style="padding: 6px; border: 1px solid #eee; text-align: center;">Web</td>
        <td style="padding: 6px; border: 1px solid #eee; text-align: center;">$(Get-StatusIcon $s_createYouthClub)</td>
        <td style="padding: 6px; border: 1px solid #eee;">Pal</td>
      </tr>
      <tr style="background: #e8f5e9;">
        <td style="padding: 6px; border: 1px solid #eee; text-align: center;">2</td>
        <td style="padding: 6px; border: 1px solid #eee;"><b>Approve Youth Club</b></td>
        <td style="padding: 6px; border: 1px solid #eee;">superAdminApprove</td>
        <td style="padding: 6px; border: 1px solid #eee;">SuperAdmin login + Search Youth Club + Approve Organization</td>
        <td style="padding: 6px; border: 1px solid #eee; text-align: center;">Web</td>
        <td style="padding: 6px; border: 1px solid #eee; text-align: center;">$(Get-StatusIcon $s_approveYouthClub)</td>
        <td style="padding: 6px; border: 1px solid #eee;">Pal</td>
      </tr>
    </table>

    <h3 style="color: #160363; margin: 0 0 12px; border-bottom: 2px solid #160363; padding-bottom: 5px;">Test Flow</h3>
    <p style="font-size: 13px; color: #555; line-height: 1.8;">
      Register 6 Members (parallel) &#8594; Login &#8594; Create Youth Club (About + Basic Info + Address + Infrastructure + Financial + Activities + Membership OTP + Establishment + Declaration + Submit) &#8594; Logout &#8594; SuperAdmin Approve
    </p>
  </div>
  <div style="background: #f8f9fa; padding: 12px; text-align: center; border-radius: 0 0 8px 8px; border: 1px solid #e0e0e0; border-top: none;">
    <p style="color: #888; font-size: 11px; margin: 0;">
      MY Bharat | QA Automation | Youth Club Module | $timestamp
    </p>
  </div>
</div>
"@

$subject = "$($config['report.subject.prefix']) Youth Club | $status | $timestamp"

try {
    $securePassword = ConvertTo-SecureString $config['smtp.password'] -AsPlainText -Force
    $cred = New-Object System.Management.Automation.PSCredential($config['smtp.username'], $securePassword)

    $mailParams = @{
        From       = $config['email.from']
        To         = $config['email.to']
        Subject    = $subject
        Body       = $htmlBody
        BodyAsHtml = $true
        SmtpServer = $config['smtp.server']
        Port       = [int]$config['smtp.port']
        UseSsl     = $true
        Credential = $cred
    }

    $extentReport = "reports\index.html"
    if (Test-Path $extentReport) {
        $mailParams['Attachments'] = (Resolve-Path $extentReport).Path
    }

    Send-MailMessage @mailParams

    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host " Youth Club Email Report Sent!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  To:      $($config['email.to'])"
    Write-Host "  Status:  $status (P=$passed F=$failed S=$skipped)"
    Write-Host "========================================" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Failed to send email - $_" -ForegroundColor Red
    exit 1
}
