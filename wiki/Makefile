PAGES =	geomlab.wiki build.wiki commands.wiki download.wiki errors.wiki \
	GeomPic.tmpl home.wiki intro.wiki \
	language.wiki library.wiki Markup.tmpl Message.tmpl \
	nojava.wiki sheet1.wiki sheet2.wiki sheet3.wiki sheet4.wiki \
	sheet5.wiki sheet6.wiki sheet7.wiki sheet8.wiki sheet9.wiki \
	start.wiki teachers.wiki tiles.wiki tips.wiki using.wiki \
	worksheets.wiki egyptian.wiki common.wiki GeomGrind.php \
	mathsymbols.wiki IfWiki.tmpl IfBook.tmpl =.tmpl \
	BigSpace.tmpl Resource.tmpl BigGreenArrow.tmpl sidebar.wiki \
	mainpage.wiki help.wiki HelpStart.tmpl contact.wiki \
	ContactForm.tmpl thanks.wiki

all: $(PAGES)

.DELETE_ON_ERROR:

publish: .publish .settings

.publish: $(PAGES)
	tclsh ../scripts/page-upload.tcl $?
	echo timestamp >$@

.settings: LocalSettings.php
	scp $< spivey:/var/www/gwiki/$<
	echo timestamp >$@

clean: force
	rm -f sourcelist.wiki

force:
