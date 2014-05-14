PAGES =	build.wiki commands.wiki download.wiki errors.wiki \
	geombox.wiki geomlab.wiki geompic.wiki home.wiki intro.wiki \
	language.wiki library.wiki markup.wiki message.wiki \
	nojava.wiki sheet1.wiki sheet2.wiki sheet3.wiki sheet4.wiki \
	sheet5.wiki sheet6.wiki sheet7.wiki sheet8.wiki sheet9.wiki \
	start.wiki teachers.wiki tiles.wiki tips.wiki using.wiki \
	worksheets.wiki egyptian.wiki

all: $(PAGES)

.DELETE_ON_ERROR:

publish: .publish

.publish: $(PAGES)
	tclsh ../scripts/page-upload.tcl $?
	echo timestamp >$@

clean: force
	rm -f sourcelist.wiki

force:
