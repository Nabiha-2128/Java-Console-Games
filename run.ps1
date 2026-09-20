$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force out | Out-Null
    $sources = @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
    & javac -encoding UTF-8 -d out $sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & java -cp out Main
    if ($LASTEXITCODE -ne 0) { throw 'Application exited with an error.' }
} finally { Pop-Location }
