# Top level Makefile for GeomLab

PACKAGES = funbase funjit geomlab plugins
JAVA := $(patsubst src/%,%,$(foreach pkg,$(PACKAGES),$(wildcard src/$(pkg)/*)))
SOURCE = $(JAVA) boot.txt compiler.txt prelude.txt 
HELP = commands errors language library tips
RESOURCES = DejaVuSansMono.ttf mike.jpg mikelet.jpg contents.html style.css \
	$(HELP:%=%.html)
IMAGES = geomlab.gls examples.gls

all: prep .compiled $(RESOURCES:%=obj/%) $(IMAGES:%=obj/%)

prep: force
	@mkdir -p obj

.compiled: $(wildcard src/*/*.java)
	javac -d obj src/*/*.java
	echo timestamp >$@

obj/%: res/%; cp $< $@
obj/%: src/%; cp $< $@

obj/%.html: wiki/%.wiki wiki/htmlframe scripts/htmltran.tcl
	tclsh scripts/htmltran.tcl $< >$@

RUNSCRIPT = java -cp obj -ea geomlab.RunScript

obj/geomlab.gls: .compiled src/boot.txt src/compiler.txt src/prelude.txt \
		$(RESOURCES:%=obj/%)
	$(RUNSCRIPT) -b src/boot.txt src/compiler.txt src/compiler.txt \
		src/prelude.txt -e '_save("$@")'

obj/examples.gls: obj/geomlab.gls src/examples.txt
	$(RUNSCRIPT) src/examples.txt -e '_save("$@")'

bootstrap: .compiled force 
	$(RUNSCRIPT) -b src/boot.txt src/compiler.txt \
		-e 'let dump = _primitive("dump") in dump("stage1.boot")'
	$(RUNSCRIPT) -b stage1.boot src/compiler.txt \
		-e 'let dump = _primitive("dump") in dump("stage2.boot")'
	$(RUNSCRIPT) -b stage2.boot src/compiler.txt \
		-e 'let dump = _primitive("dump") in dump("stage3.boot")'
	cmp stage2.boot stage3.boot

bootup: stage2.boot force
	(sed -e 's/compiler.txt/boot.txt/' -e '/^ *$$/q' src/compiler.txt; \
		cat stage2.boot) >src/boot.txt


# Web resources

PREFIX = http://spivey.oriel.ox.ac.uk/wiki/files/gl

web: web-dirs update .signed

web-dirs: force
	@mkdir -p web web/src $(PACKAGES:%=web/src/%)

update: web/.htaccess web/geomlab.jar web/examples.jar web/geomlab-src.jar \
		web/geomlab.jnlp web/geomdemo.jnlp \
		$(SOURCE:%=web/src/%) web/arrow.png

web/geomlab.jar: .compiled obj/geomlab.gls $(RESOURCES:%=obj/%)
	cd obj; jar cfm ../$@ ../scripts/manifest \
		$(PACKAGES) $(RESOURCES) geomlab.gls

web/examples.jar: obj/examples.gls
	cd obj; jar cf ../$@ examples.gls

.signed: web/geomlab.jar web/examples.jar
	for f in $?; do jarsigner -storepass `cat ~/.keypass` $$f mykey; done
	echo timestamp >$@

web/geomlab-src.jar: $(SOURCE:%=src/%) $(RESOURCES:%=obj/%)
	jar cf $@ $^

web/.htaccess: scripts/htaccess;		cp $< $@
web/src/%: src/%;				cp $< $@
web/%: res/%;					cp $< $@

web/%.jnlp: scripts/%.jnlp.in
	sed 's=@CODEBASE@=$(PREFIX)=' $< >$@

publish: web force
	rsync -rvt --delete web/ spivey:wiki/files/gl

clean: force
	rm -rf obj
	rm -f .compiled .signed

force:
