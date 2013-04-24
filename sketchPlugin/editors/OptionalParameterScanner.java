package sketchPlugin.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import sketchPlugin.editors.SketchCodeScanner.TextWords;

public class OptionalParameterScanner  extends RuleBasedScanner{
	public OptionalParameterScanner(ColorManager manager){
		IToken defaultToken = new Token(new TextAttribute(manager.getColor(IColorConstants.DEFAULT),null,SWT.ITALIC));
		IToken intToken = new Token(new TextAttribute(manager.getColor(IColorConstants.TYPE),null,SWT.BOLD));
		WordRule wordRule = new WordRule(new TextWords(),defaultToken);
		wordRule.addWord("int", intToken);
		IRule[] rule = new IRule[1];
		rule[0]= wordRule;
	}

}
