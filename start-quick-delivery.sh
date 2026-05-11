#!/usr/bin/env bash
# ==========================================
#   Quick Delivery - Launcher Script (Linux)
#   Inicia todos los microservicios y frontend
# ==========================================

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ── Colores ANSI ──────────────────────────────────────────
RESET='\033[0m'
BOLD='\033[1m'
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
MAGENTA='\033[0;35m'
RED='\033[0;31m'
DARK_GRAY='\033[0;90m'
WHITE='\033[0;97m'

# ── Definición de servicios ───────────────────────────────
# Formato: "Nombre|Color|Ruta|Comando"
declare -a SERVICES=(
    "Delivery Service|${CYAN}|$ROOT/backend/Quick-Delivery-Delivery/Quick-Delivery-Delivery|./gradlew bootRun"
    "Edge Service|${GREEN}|$ROOT/backend/Quick-Delivery-Edge/Quick-Delivery-Edge|./gradlew bootRun"
    "Restaurant Service|${YELLOW}|$ROOT/backend/Quick-Delivery-Restaurant/Quick-Delivery-Restaurant|./gradlew bootRun"
    "Client Service|${MAGENTA}|$ROOT/backend/Quick-Delivery-Client/Quick-Delivery-Client|./gradlew bootRun"
    "Frontend (Angular)|${RED}|$ROOT/frontend|ng serve"
)

# ── Banner ────────────────────────────────────────────────
print_banner() {
    clear
    echo ""
    echo -e "  ${CYAN}██████╗ ██╗   ██╗██╗ ██████╗██╗  ██╗    ██████╗ ███████╗██╗     ██╗██╗   ██╗███████╗██████╗ ██╗   ██╗${RESET}"
    echo -e "  ${CYAN}██╔═══██╗██║   ██║██║██╔════╝██║ ██╔╝    ██╔══██╗██╔════╝██║     ██║██║   ██║██╔════╝██╔══██╗╚██╗ ██╔╝${RESET}"
    echo -e "  ${CYAN}██║   ██║██║   ██║██║██║     █████╔╝     ██║  ██║█████╗  ██║     ██║██║   ██║█████╗  ██████╔╝ ╚████╔╝ ${RESET}"
    echo -e "  ${CYAN}██║▄▄ ██║██║   ██║██║██║     ██╔═██╗     ██║  ██║██╔══╝  ██║     ██║╚██╗ ██╔╝██╔══╝  ██╔══██╗  ╚██╔╝  ${RESET}"
    echo -e "  ${CYAN}╚██████╔╝╚██████╔╝██║╚██████╗██║  ██╗    ██████╔╝███████╗███████╗██║ ╚████╔╝ ███████╗██║  ██║   ██║   ${RESET}"
    echo -e "  ${CYAN} ╚══▀▀═╝  ╚═════╝ ╚═╝ ╚═════╝╚═╝  ╚═╝    ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝   ╚═╝  ${RESET}"
    echo ""
    echo -e "  ${CYAN}Launcher de Microservicios  v1.0${RESET}"
    echo -e "  ${DARK_GRAY}─────────────────────────────────────────────────────────────────────────────${RESET}"
    echo ""
}

print_status() {
    local name="$1"
    local color="$2"
    local path="$3"
    echo -e "  ${DARK_GRAY}[${RESET} ${color}${name}${RESET} ${DARK_GRAY}]${RESET} ${WHITE}${path}${RESET}"
}

# ── Función para abrir terminal con el servicio ───────────
launch_service() {
    local name="$1"
    local path="$2"
    local cmd="$3"

    # Detectar el emulador de terminal disponible
    if command -v gnome-terminal &>/dev/null; then
        gnome-terminal --title="$name" -- bash -c "
            echo -e '\033[0;36m═══════════════════════════════════════\033[0m'
            echo -e '\033[0;36m  $name\033[0m'
            echo -e '\033[0;90m  Directorio: $path\033[0m'
            echo -e '\033[0;36m═══════════════════════════════════════\033[0m'
            echo ''
            cd '$path' && $cmd
            echo ''
            echo 'Servicio detenido. Presiona ENTER para cerrar.'
            read
        " &
    elif command -v xterm &>/dev/null; then
        xterm -title "$name" -e bash -c "
            echo '═══════════════════════════════════════'
            echo '  $name'
            echo '  Directorio: $path'
            echo '═══════════════════════════════════════'
            echo ''
            cd '$path' && $cmd
            echo ''
            echo 'Servicio detenido. Presiona ENTER para cerrar.'
            read
        " &
    elif command -v konsole &>/dev/null; then
        konsole --new-tab -p tabtitle="$name" -e bash -c "
            cd '$path' && $cmd; read
        " &
    elif command -v tilix &>/dev/null; then
        tilix -t "$name" -e bash -c "
            cd '$path' && $cmd; read
        " &
    else
        # Sin GUI: correr en background y redirigir logs a archivo
        echo -e "  ${YELLOW}[AVISO]${RESET} No se detectó emulador de terminal gráfico."
        echo -e "  ${DARK_GRAY}Iniciando '$name' en background → logs en /tmp/${name// /_}.log${RESET}"
        mkdir -p /tmp/quick-delivery-logs
        (cd "$path" && $cmd > "/tmp/quick-delivery-logs/${name// /_}.log" 2>&1) &
        echo $! >> /tmp/quick-delivery-pids.txt
    fi
}

# ═══════════════════════════════════════
#  INICIO DEL SCRIPT
# ═══════════════════════════════════════

print_banner

echo -e "  ${WHITE}Servicios que se van a iniciar:${RESET}"
echo ""

for svc in "${SERVICES[@]}"; do
    IFS='|' read -r name color path cmd <<< "$svc"
    print_status "$name" "$color" "$path"
done

echo ""
echo -e "  ${DARK_GRAY}─────────────────────────────────────────────────────────────────────────────${RESET}"
echo ""
echo -e "  ${DARK_GRAY}Cada servicio se abrirá en una nueva terminal.${RESET}"
echo -e "  ${YELLOW}Presiona ENTER para iniciar todos los servicios...${RESET}"
echo ""
read -r -p "  > "

echo ""
echo -e "  ${GREEN}Iniciando servicios...${RESET}"
echo ""

# Limpiar archivo de PIDs si existe
rm -f /tmp/quick-delivery-pids.txt

index=1
total=${#SERVICES[@]}

for svc in "${SERVICES[@]}"; do
    IFS='|' read -r name color path cmd <<< "$svc"

    # Verificar que la ruta existe
    if [ ! -d "$path" ]; then
        echo -e "  ${RED}[ADVERTENCIA]${RESET} Ruta no encontrada para ${color}${name}${RESET}: $path"
        echo -e "               Verifica la ruta en el script."
        echo ""
        ((index++))
        continue
    fi

    launch_service "$name" "$path" "$cmd"

    echo -e "  ${DARK_GRAY}($index/$total)${RESET} ${color}${name}${RESET} ${GREEN}-> Iniciado correctamente${RESET}"

    ((index++))
    sleep 0.8
done

echo ""
echo -e "  ${DARK_GRAY}─────────────────────────────────────────────────────────────────────────────${RESET}"
echo ""
echo -e "  ${GREEN}¡Todos los servicios han sido lanzados exitosamente!${RESET}"
echo ""
echo -e "  ${WHITE}SERVICIOS ACTIVOS:${RESET}"
echo ""

for svc in "${SERVICES[@]}"; do
    IFS='|' read -r name color path cmd <<< "$svc"
    echo -e "   ${DARK_GRAY}•${RESET} ${color}${name}${RESET}"
done

echo ""
echo -e "  ${DARK_GRAY}─────────────────────────────────────────────────────────────────────────────${RESET}"
echo ""
echo -e "  ${DARK_GRAY}Para detener los servicios, cierra cada terminal o usa Ctrl+C en cada una.${RESET}"
echo ""
read -r -p "  Presiona ENTER para cerrar este launcher  "
echo ""
