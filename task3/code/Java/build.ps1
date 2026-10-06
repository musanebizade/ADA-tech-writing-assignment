$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
$jar = "lib/junit-platform-console-standalone.jar"
if (-not (Test-Path $jar)) {
    New-Item -ItemType Directory -Force lib | Out-Null
    Invoke-WebRequest "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/junit-platform-console-standalone-1.11.4.jar" -OutFile $jar
}
New-Item -ItemType Directory -Force out | Out-Null
javac -d out -cp $jar (Get-ChildItem -Recurse src, test -Filter *.java).FullName
if ($LASTEXITCODE) { exit 1 }
java -jar $jar execute -cp out --scan-classpath out