set scriptdir [file dirname [info script]]

source "$scriptdir/login.tcl"
source "$scriptdir/common-upload.tcl"
source "$scriptdir/../wiki/titles"

proc page-upload {page} {
    global title
    set text [file-data $page]
    set key [file rootname $page]
    upload $title($key) $text
}

foreach f $argv { page-upload $f }
