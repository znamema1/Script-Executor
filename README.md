# Script-Executor-Service

Web-application providing functionality to run script chains by simply passing their script repositories and input. The scripts are being run in a virtualized Docker environment.

For external system an API endpoint based on REST is published.

For a script to be executed all necessary dependencies have to be targetted for download in the dockerfile. The input is passed via standard input and output is collected from standard output of the script.

======

Webová aplikace umožnující spouštění zřetězených skriptů pouhým uvedením jejich Git repositáře. Skripty jsou spouštěny ve virtualizovaném prostředí Docker.

Pro okolní systémy vystavuje REST API. 

Aby mohl být skript spuštěň je nutné aby byla jeho technologie přidána v dockerfile.  A aby uměl číst standardní vstup a uměl zapisovat na standardní výstup.