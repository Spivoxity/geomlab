set scriptdir [file dirname [info script]]

source "$scriptdir/login.tcl"
source "$scriptdir/common-upload.tcl"
source "$scriptdir/common.tcl"
source "$scriptdir/../wiki/titles"

proc page-upload {page} {
    set name [file tail $page]
    set text [file-data $page]
    set title [get-title $name $text]

    puts "$page --> $title"

    upload $title $text
}

foreach f $argv { page-upload $f }
