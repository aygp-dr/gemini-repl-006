# Common utilities for scripts

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Detect OS
detect_os() {
    case "$(uname -s)" in
        Darwin*) echo "macos";;
        Linux*) echo "linux";;
        FreeBSD*) echo "freebsd";;
        *) echo "unknown";;
    esac
}

# Get appropriate make command
get_make_cmd() {
    if [[ "$(detect_os)" == "freebsd" ]]; then
        echo "gmake"
    else
        echo "make"
    fi
}
