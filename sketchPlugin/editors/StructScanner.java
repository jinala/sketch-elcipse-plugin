package sketchPlugin.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;
import org.eclipse.swt.SWT;


public class StructScanner extends RuleBasedScanner {
	
	class StructWordDetector implements IWordDetector {

			public boolean isWordStart(char c) {
				
				return Character.isLetter(c);
			}

			
			public boolean isWordPart(char c) {
				return Character.isLetter(c);
			}
		}
	
	
	public StructScanner(ColorManager manager) {
		String[] Keywords= {"struct"};
		String[] Types= { "void", "boolean", "char", "int", "float", "double","bit", "fun" }; 

		 String[] Constants= { "false", "null", "true" }; 
		
		
		IToken defaultToken = 
				new Token(
						new TextAttribute(
							manager.getColor(IColorConstants.DEFAULT)));	
		
		IToken commentToken1 =
				new Token(
						new TextAttribute(manager.getColor(IColorConstants.COMMENT)));
		IToken keyToken1 = 
				new Token(
						new TextAttribute(
							manager.getColor(IColorConstants.KEYWORD),null, SWT.BOLD));
		IToken typeToken1 = new Token(
						new TextAttribute(
							manager.getColor(IColorConstants.TYPE),null,SWT.BOLD));
		IToken stringToken1 = new Token(
						new TextAttribute(
							manager.getColor(IColorConstants.STRING)));
				

		List rules= new ArrayList();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", commentToken1)); 

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", stringToken1, '\\')); 
		rules.add(new SingleLineRule("'", "'", stringToken1, '\\')); 

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new SketchWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule= new WordRule(new SketchCodeScanner.TextWords(),defaultToken);
		for (int i= 0; i < Keywords.length; i++)
			wordRule.addWord(Keywords[i], keyToken1);
		for (int i= 0; i < Types.length; i++)
			wordRule.addWord(Types[i], typeToken1);
		for (int i= 0; i < Constants.length; i++)
			wordRule.addWord(Constants[i], typeToken1);
		rules.add(wordRule);
		
		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

}
