param(
  [Parameter(Mandatory)][string]$Version,
  [string]$BaseUrl = 'https://github.com/NTU-KhPI-CMPS/thesis_checker/releases/latest/download',
  [string]$App = 'thesis_checker'
)
$ErrorActionPreference = 'Stop'
Set-Location "$PSScriptRoot\..\thesis_checker_app"

dart run msix:build --version $Version; if ($LASTEXITCODE) { exit 1 }

$dir = (Resolve-Path 'build\windows\x64\runner\Release').Path
$manifest = "$dir\AppxManifest.xml"
$id   = ([xml](Get-Content $manifest -Raw)).Package.Identity
$pub  = [Security.SecurityElement]::Escape($id.Publisher)
$msix = "${App}"

# Manifest: uap13 namespace + AutoUpdate entry
$m = Get-Content $manifest -Raw
$m = $m -replace '<Package\s', '<Package xmlns:uap13="http://schemas.microsoft.com/appx/manifest/uap/windows10/13" '
$m = if ($m -match 'IgnorableNamespaces="') { $m -replace 'IgnorableNamespaces="', 'IgnorableNamespaces="uap13 ' }
     else { $m -replace '<Package\s', '<Package IgnorableNamespaces="uap13" ' }
$m = $m -replace '</Properties>', '<uap13:AutoUpdate><uap13:AppInstaller File="Update.appinstaller" /></uap13:AutoUpdate></Properties>'
Set-Content $manifest $m -Encoding UTF8

# App Installer file: embedded in the package + copy to host
$ai = @"
<?xml version="1.0" encoding="utf-8"?>
<AppInstaller xmlns="http://schemas.microsoft.com/appx/appinstaller/2018" Version="$Version" Uri="$BaseUrl/$App.appinstaller">
  <MainPackage Name="$($id.Name)" Publisher="$pub" Version="$Version" ProcessorArchitecture="$($id.ProcessorArchitecture)" Uri="$BaseUrl/$msix.msix" />
  <UpdateSettings>
    <OnLaunch HoursBetweenUpdateChecks="0" ShowPrompt="true" UpdateBlocksActivation="false" />
    <AutomaticBackgroundTask />
  </UpdateSettings>
</AppInstaller>
"@
New-Item dist -ItemType Directory -Force | Out-Null
Set-Content "$dir\Update.appinstaller" $ai -Encoding UTF8
Set-Content "dist\$App.appinstaller" $ai -Encoding UTF8

dart run msix:pack --version $Version --output-path "$PWD\dist" --output-name $msix; if ($LASTEXITCODE) { exit 1 }

signtool sign /v /debug /fd SHA256 /tr http://timestamp.acs.microsoft.com /td SHA256 `
    /dlib "C:\Program Files\signtool\Azure.CodeSigning.Dlib.dll" /dmdf ..\tools\signing.json "dist\$msix.msix"

Set-Location "$PSScriptRoot\.."
