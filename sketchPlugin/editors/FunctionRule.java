package sketchPlugin.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class FunctionRule extends MultiLineRule {
	int openParan = 0;
	int closeParan=1;
	
	int count = 0;
	
	public FunctionRule(String start, String end, IToken token, char c, boolean EOFallowed){
		super(start, end, token, c, EOFallowed);
		
	}
	@Override
	protected boolean sequenceDetected(
			ICharacterScanner scanner,
			char[] sequence,
			boolean eofAllowed) {			
		int c = scanner.read();	

		
		if (sequence[0] !='}') {

			if(c ==';'||c ==scanner.EOF){	

				scanner.unread();
				for (int i = 0;i<count;i++){
					scanner.unread();
				}
				count=0;
				return false;
			}else if (c=='{'){
				
				scanner.unread();
				
				for(int i = 0;i<count;i++){
					scanner.unread();
				}
				
				count =0;
				return super.sequenceDetected(scanner, sequence, eofAllowed);
			}else{
				count++;
				return sequenceDetected(scanner, sequence, eofAllowed);
			}
		} else if (sequence[0] == '}') {
			scanner.unread();
		}
		
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner){
		
		int c = scanner.read();
		if(c==scanner.EOF){
			openParan =0;
			closeParan =1;
			return false;
		}
		else if(c=='}'){
			if(openParan==closeParan){
				openParan =0;
				closeParan =1;
				
				return true;
			}else{
				closeParan++;
				return endSequenceDetected(scanner);
			}

		}
		else if(c=='{'){
			openParan++;
			return endSequenceDetected(scanner);

		}else{
			return endSequenceDetected(scanner);
		}


	}
	

}

