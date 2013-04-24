package sketchPlugin.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class OptionalParameterRule extends MultiLineRule {
	SketchWhitespaceDetector spaceDetector = new SketchWhitespaceDetector();
	int count = 0;
	boolean commaEncountered = false;
	public OptionalParameterRule(IToken token){
		super("[","]",token);
	}
	@Override 
	protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {
		int c =scanner.read();
		if(sequence[0]=='['){
			
			if(spaceDetector.isWhitespace((char)c)){
				count++;
				return sequenceDetected(scanner, sequence, eofAllowed);
			}else if(c==','&&!commaEncountered){
				count++;
				commaEncountered = true;
				return sequenceDetected(scanner, sequence, eofAllowed);
			}else if(c==']'&&commaEncountered){
				scanner.unread();
				for(int i = 0;i<count;i++){
					scanner.unread();
				}
				count =0;
				commaEncountered = false;
				return super.sequenceDetected(scanner, sequence, eofAllowed);
			}else if (c==']'||c==scanner.EOF||commaEncountered){
				scanner.unread();
				for(int i = 0; i<count;i++){
					scanner.unread();
				}
				count =0;
				commaEncountered = false;
				return false;
			}
			else{
				count++;
				return sequenceDetected(scanner, sequence, eofAllowed);
			}
			
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}

}
