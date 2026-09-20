$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force out | Out-Null
    $sources = @(Get-ChildItem src,tests -Recurse -Filter *.java | ForEach-Object { $_.FullName })
    & javac -Xlint:all -encoding UTF-8 -d out $sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & java -cp out GameTests
    if ($LASTEXITCODE -ne 0) { throw 'Regression tests failed.' }
    Write-Host 'For interactive integration tests, run: python tests/integration_test.py'
} finally { Pop-Location }
