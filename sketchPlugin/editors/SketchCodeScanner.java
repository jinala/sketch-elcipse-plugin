package sketchPlugin.editors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;


public class SketchCodeScanner extends RuleBasedScanner {
	private static String[] fgKeywords= {"atomic","fork","insert", "into","loop","repeat","struct","minrepeat","new","reorder","template","ref","if","else","while","for","switch","case","default","break","do","continue","return","parfor","until","by","implements","assert","asset_max","h_assert","generator","harness","model","library","printfcn","device","global","serial","spmdfork","stencil","include","pragma","package"  }; 

	private static String[] fgTypes= { "void", "boolean", "char", "int", "float", "double","bit", "fun" }; 

	private static String[] fgConstants= { "false", "null", "true" }; 

	static class TextWords implements IWordDetector {

		@Override
		public boolean isWordStart(char c) {
			return c == '?' || Character.isLetter(c);
		}

		@Override
		public boolean isWordPart(char c) {
			return c == '?' || Character.isLetter(c);
		}

	}

	@SuppressWarnings("unchecked")
	public SketchCodeScanner( ColorManager manager) {
		IToken defaultToken = 
				new Token(
						new TextAttribute(
								manager.getColor(IColorConstants.DEFAULT)));	

		IToken holeToken = 
				new Token(
						new TextAttribute(
								manager.getColor(IColorConstants.HOLE),null,SWT.BOLD));
		IToken commentToken =
				new Token(
						new TextAttribute(manager.getColor(IColorConstants.COMMENT)));
		IToken keyToken = 
				new Token(
						new TextAttribute(
								manager.getColor(IColorConstants.KEYWORD),null, SWT.BOLD));
		IToken typeToken = new Token(
				new TextAttribute(
						manager.getColor(IColorConstants.TYPE),null,SWT.BOLD));
		IToken stringToken = new Token(
				new TextAttribute(
						manager.getColor(IColorConstants.STRING)));


		IToken annotationToken = new Token(
				new TextAttribute(
						manager.getColor(IColorConstants.ANNOTATION)));


		List rules= new ArrayList();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", commentToken)); 

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", stringToken, '\\')); 
		rules.add(new SingleLineRule("'", "'", stringToken, '\\')); 

		rules.add(new SingleLineRule("@","(",annotationToken,'\\'){
			@Override
			protected boolean endSequenceDetected(ICharacterScanner scanner){
				int c = scanner.read();

				if(c=='('){
					scanner.unread();
					return true;
				}else if(c ==scanner.EOF){
					return false;
				}else{
					return endSequenceDetected(scanner);
				}


			}
		});
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new SketchWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule= new WordRule(new TextWords(),defaultToken);
		for (int i= 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], keyToken);
		for (int i= 0; i < fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], typeToken);
		for (int i= 0; i < fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], typeToken);
		wordRule.addWord("??",holeToken);
		rules.add(wordRule);

		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}


}
