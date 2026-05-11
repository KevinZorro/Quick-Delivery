# ==========================================
#   Quick Delivery - Launcher Script
#   Inicia todos los microservicios y frontend
# ==========================================

$ROOT = "C:\Users\kevin\Desktop\Enginer\Sw\Quick-Delivery"

$SERVICES = @(
    @{
        Name    = "Delivery Service"
        Color   = "Cyan"
        Path    = "$ROOT\backend\Quick-Delivery-Delivery\Quick-Delivery-Delivery"
        Command = ".\gradlew bootRun"
    },
    @{
        Name    = "Edge Service"
        Color   = "Green"
        Path    = "$ROOT\backend\Quick-Delivery-edge\quick-Delivery-edge"
        Command = ".\gradlew bootRun"
    },
    @{
        Name    = "Restaurant Service"
        Color   = "Yellow"
        Path    = "$ROOT\backend\Quick-Delivery-restaurant\quick-Delivery-restaurant"
        Command = ".\gradlew bootRun"
    },
    @{
        Name    = "Client Service"
        Color   = "Magenta"
        Path    = "$ROOT\backend\Quick-Delivery-client\quick-Delivery-client"
        Command = ".\gradlew bootRun"
    },
    @{
        Name    = "Frontend (Angular)"
        Color   = "Red"
        Path    = "$ROOT\frontend"
        Command = "ng serve"
    }
)

function Write-Banner {
    Clear-Host
    Write-Host ""
    Write-Host "  ██████╗ ██╗   ██╗██╗ ██████╗██╗  ██╗    ██████╗ ███████╗██╗     ██╗██╗   ██╗███████╗██████╗ ██╗   ██╗" -ForegroundColor Cyan
    Write-Host "  ██╔═══██╗██║   ██║██║██╔════╝██║ ██╔╝    ██╔══██╗██╔════╝██║     ██║██║   ██║██╔════╝██╔══██╗╚██╗ ██╔╝" -ForegroundColor Cyan
    Write-Host "  ██║   ██║██║   ██║██║██║     █████╔╝     ██║  ██║█████╗  ██║     ██║██║   ██║█████╗  ██████╔╝ ╚████╔╝ " -ForegroundColor Cyan
    Write-Host "  ██║▄▄ ██║██║   ██║██║██║     ██╔═██╗     ██║  ██║██╔══╝  ██║     ██║╚██╗ ██╔╝██╔══╝  ██╔══██╗  ╚██╔╝  " -ForegroundColor Cyan
    Write-Host "  ╚██████╔╝╚██████╔╝██║╚██████╗██║  ██╗    ██████╔╝███████╗███████╗██║ ╚████╔╝ ███████╗██║  ██║   ██║   " -ForegroundColor Cyan
    Write-Host "   ╚══▀▀═╝  ╚═════╝ ╚═╝ ╚═════╝╚═╝  ╚═╝    ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝   ╚═╝   " -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  Launcher de Microservicios  v1.0" -ForegroundColor DarkCyan
    Write-Host "  ─────────────────────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
    Write-Host ""
}

function Write-Status {
    param([string]$Name, [string]$Status, [string]$Color)
    Write-Host "  [" -NoNewline -ForegroundColor DarkGray
    Write-Host " $Name " -NoNewline -ForegroundColor $Color
    Write-Host "] " -NoNewline -ForegroundColor DarkGray
    Write-Host $Status -ForegroundColor White
}

Write-Banner

Write-Host "  Servicios que se van a iniciar:" -ForegroundColor White
Write-Host ""

foreach ($svc in $SERVICES) {
    Write-Status -Name $svc.Name -Status $svc.Path -Color $svc.Color
}

Write-Host ""
Write-Host "  ─────────────────────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  Cada servicio se abrira en una nueva ventana de PowerShell." -ForegroundColor DarkGray
Write-Host "  Presiona ENTER para iniciar todos los servicios..." -ForegroundColor Yellow
Write-Host ""
Read-Host "  > "

Write-Host ""
Write-Host "  Iniciando servicios..." -ForegroundColor Green
Write-Host ""

$index = 1
foreach ($svc in $SERVICES) {
    $name    = $svc.Name
    $path    = $svc.Path
    $command = $svc.Command
    $color   = $svc.Color

    # Verifica si la ruta existe antes de abrir la ventana
    if (-Not (Test-Path $path)) {
        Write-Host "  [ADVERTENCIA] Ruta no encontrada para $name : $path" -ForegroundColor Red
        Write-Host "               Verifica la ruta y ajusta el script si es necesario." -ForegroundColor DarkRed
        Write-Host ""
        continue
    }

    $scriptBlock = "Set-Location '$path'; Write-Host '═══════════════════════════════════════' -ForegroundColor $color; Write-Host '  $name' -ForegroundColor $color; Write-Host '  Directorio: $path' -ForegroundColor DarkGray; Write-Host '═══════════════════════════════════════' -ForegroundColor $color; Write-Host ''; $command"

    Start-Process powershell -ArgumentList "-NoExit", "-Command", $scriptBlock `
        -WindowStyle Normal

    Write-Host "  ($index/$($SERVICES.Count)) " -NoNewline -ForegroundColor DarkGray
    Write-Host "$name " -NoNewline -ForegroundColor $color
    Write-Host "-> Iniciado correctamente" -ForegroundColor Green

    $index++
    Start-Sleep -Milliseconds 800
}

Write-Host ""
Write-Host "  ─────────────────────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  Todos los servicios han sido lanzados exitosamente!" -ForegroundColor Green
Write-Host ""
Write-Host "  SERVICIOS ACTIVOS:" -ForegroundColor White
Write-Host ""

foreach ($svc in $SERVICES) {
    Write-Host "   • " -NoNewline -ForegroundColor DarkGray
    Write-Host $svc.Name -ForegroundColor $svc.Color
}

Write-Host ""
Write-Host "  ─────────────────────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  Para detener los servicios, cierra cada ventana de PowerShell." -ForegroundColor DarkGray
Write-Host ""
Read-Host "  Presiona ENTER para cerrar este launcher"
