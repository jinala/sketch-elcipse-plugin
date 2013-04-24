package sketchPlugin.editors;


import java.util.Arrays;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

public class StructRule extends MultiLineRule {
	
	public StructRule(IToken token){
		super("struct","}",token,(char)0 , true);
		
	
	}
	@Override
	protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
		//System.out.println(sequence);
		
		int c =scanner.read();
		//System.out.println((char) c);
		if(sequence[0]=='s'){
			scanner.unread();
		}else if(sequence[0]=='}'){
			scanner.unread();
		}
		//System.out.println(super.sequenceDetected(scanner, sequence, eofAllowed));
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
	
	
	
	
}
