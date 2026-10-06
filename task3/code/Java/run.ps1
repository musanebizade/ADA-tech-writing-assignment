Set-Location $PSScriptRoot
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
if ($LASTEXITCODE) { exit 1 }
java -cp out ada.matmul.Main @args