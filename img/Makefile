FILES = GeomLab-1.png GeomLab-2.png GeomLab-3.png GeomLab-4.png \
	GeomLab-5.png GeomLab-talk.ppt

all:

publish: .publish

.publish: $(FILES)
	tclsh ../scripts/image-upload.tcl $?
	echo timestamp >$@
