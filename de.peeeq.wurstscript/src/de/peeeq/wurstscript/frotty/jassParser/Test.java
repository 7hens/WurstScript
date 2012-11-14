package de.peeeq.wurstscript.frotty.jassParser;

import java.io.File;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.peeeq.wurstscript.Pjass;
import de.peeeq.wurstscript.WLogger;
import de.peeeq.wurstscript.Pjass.Result;
import de.peeeq.wurstscript.frotty.jassValidator.JassErrors;
import de.peeeq.wurstscript.jassAst.JassAst;
import de.peeeq.wurstscript.jassAst.JassProg;
import de.peeeq.wurstscript.jassAst.JassProgs;
import de.peeeq.wurstscript.jassinterpreter.TestFailException;
import de.peeeq.wurstscript.jassoptimizer.JassOptimizerImpl;
import de.peeeq.wurstscript.jassprinter.JassPrinter;

public class Test {
	
	static frottyjassParser parser = new frottyjassParser(null);
	private static JassProgs progs;
	public static void main(String ... args) {
		try {
			String inputFile = args[0];
			
			// read/parse a file
			progs = JassAst.JassProgs();
			
			JassProg prog;
			//common.j+blizzard.j
			try {
				prog = parseFile("C:/Users/Frotty/Documents/GitHub/WurstScript/de.peeeq.wurstscript/src/de/peeeq/wurstscript/frotty/jassParser/common.j");
				System.out.println("common.j parsed...");
				File common = new File("common_f.j");
				
				StringBuilder sb = new StringBuilder();
				new JassPrinter(true).printProg(sb, prog);
				Files.write(sb.toString(), common, Charsets.UTF_8);
				
				prog = parseFile("C:/Users/Frotty/Documents/GitHub/WurstScript/de.peeeq.wurstscript/src/de/peeeq/wurstscript/frotty/jassParser/Blizzard.j");
				System.out.println("Blizzard.j parsed...");
				File bliz = new File("bliz_f.j");
				
				sb = new StringBuilder();
				new JassPrinter(true).printProg(sb, prog);
				Files.write(sb.toString(), bliz, Charsets.UTF_8);
				prog = parseFile(inputFile);
			} catch (Throwable t) {
				prog = null;
				WLogger.severe(t);
			}
			
			// print the errors
			for (String err : parser.getErrors()) {
				System.out.println(err);
			}
			if (parser.getErrors().isEmpty()) {
				System.out.println("file OK!");
			} else {
				return;
			}
			
			/// check prog
			System.out.println("Validating");
			prog.validate();
			
			if (JassErrors.errorCount() > 0) {
				for (String err : JassErrors.getErrors()) {
					System.out.println(err);
				}
				return;
			}
			
			
				
			System.out.println("Validated!");
			
			JassOptimizerImpl jp = new JassOptimizerImpl();
			jp.optimize(prog);
			
			File outputFile = new File("frottyJassTest.j");
			
			StringBuilder sb = new StringBuilder();
			new JassPrinter(false).printProg(sb, prog);
			Files.write(sb.toString(), outputFile, Charsets.UTF_8);

			// run pjass:
			System.out.println("Pjass...");
			Result pJassResult = Pjass.runPjass(outputFile);
			System.out.println(pJassResult.getMessage());
			if (!pJassResult.isOk()) {
				throw new TestFailException(pJassResult.getMessage());
			}
			
		} catch (IOException e) {
			WLogger.severe(e);
		}

	}

	private static JassProg parseFile(String inputFile) throws IOException, RecognitionException {
		frottyjassLexer lexer = new frottyjassLexer(new ANTLRFileStream(inputFile));
		CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(lexer);
		parser.setTokenStream(tokens);
		System.out.println("OOOOOOOOOOOOOOO");
		parser.toString();
		System.out.println("OOOOOOOOOOOOOOO1");
		return parser.file(progs);
	}
}
