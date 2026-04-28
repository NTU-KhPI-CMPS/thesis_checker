@echo off

set installer=build\windows\x64\runner\Release\thesis_checker.msix

call signtool sign /v /debug /fd SHA256 /tr http://timestamp.acs.microsoft.com /td SHA256 ^
    /dlib "C:\Program Files\signtool\Azure.CodeSigning.Dlib.dll" /dmdf tools\signing.json %installer%
