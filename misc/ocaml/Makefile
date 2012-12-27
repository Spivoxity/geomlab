all: interp

INTERP = print.cmo types.cmo environment.cmo \
	lexer.cmo parser.cmo interp.cmo prims.cmo main.cmo
interp: $(INTERP)
	ocamlc -g -o $@ $^

%.ml %.mli: %.mly $(MYACC)
	ocamlyacc -v $<

%.ml: %.mll
	ocamllex -o $@ $<

%.cmi: %.mli
	ocamlc -c -g -o $@ $<

%.cmo: %.ml
	ocamlc -c -g -o $@ $<

clean: force
	rm -f *.cmi *.cmo interp lexer.ml parser.ml parser.mli

force:

interp.cmo: interp.cmi environment.cmi print.cmi
interp.cmi: types.cmi
types.cmo: types.cmi
lexer.cmo: lexer.cmi types.cmi
lexer.cmi: parser.cmi
parser.cmi: types.cmi
parser.cmo: parser.cmi types.cmi
environment.cmo: environment.cmi
environment.cmi: types.cmi
print.cmo: print.cmi
types.cmi: print.cmi
main.cmo: interp.cmi lexer.cmi parser.cmi environment.cmi
prims.cmo: types.cmi environment.cmi
