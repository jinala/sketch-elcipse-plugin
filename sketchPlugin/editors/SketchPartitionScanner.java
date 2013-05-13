package sketchPlugin.editors;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;


public class SketchPartitionScanner extends RuleBasedPartitionScanner{
	public final static String COMMENT = "__comment";
	public final static String STRUCT = "__struct";
	public final static String FUNCTION = "__function";
	public final static String[] SKETCH_PARTITION_TYPES = new String[] { COMMENT,FUNCTION,STRUCT};
	
	/**
	 * Detector for empty comments.
	 */
	static class EmptyCommentDetector implements IWordDetector {		
		public boolean isWordStart(char c) {
			return (c == '/');
		}		
		public boolean isWordPart(char c) {
			return (c == '*' || c == '/');
		}
	}
	static class WordPredicateRule extends WordRule implements IPredicateRule{
		private IToken fSuccessToken;

		public WordPredicateRule(IToken successToken) {
			super(new EmptyCommentDetector());
			fSuccessToken= successToken;
			addWord("/**///", fSuccessToken); 
		}
		/*
		 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(ICharacterScanner, boolean)
		 */
		public IToken evaluate(ICharacterScanner scanner, boolean resume) {
			return super.evaluate(scanner);
		}

		/*
		 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
		 */
		public IToken getSuccessToken() {
			return fSuccessToken;
		}
	}
	
	
	

	@SuppressWarnings("unchecked")
	public SketchPartitionScanner(IDocument document){
		
		
		IToken comment = new Token(COMMENT);
		IToken struct = new Token(STRUCT);
		IToken function = new Token(FUNCTION);
	
		List rules = new ArrayList();
		
		//Add rule for single line comments.
				rules.add(new EndOfLineRule("//", Token.UNDEFINED)); //$NON-NLS-1$

				// Add rule for strings and character constants.
				rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\')); 
				rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\')); 

				// Add special case word rule.
			rules.add(new WordPredicateRule(comment));
			
				// Add rules for multi-line comments and struct
				
				rules.add(new MultiLineRule("/*", "*/", comment)); 
				String[] fgTypes= { "void", "boolean", "char", "int", "float", "double","bit", "fun", "harness","generator","stencil" };
				List<String> structTypes = new ArrayList<String>();
				int l = document.getLength();
				try {
					for(ITypedRegion i : document.computePartitioning(0, l)){
						if(i.getType().equals("__struct")){
							int offset= i.getOffset();
							int length= i.getLength();		
							String str = document.get(offset, length);
							String[]  split = str.split("[ (){}\n\t\r]");
							for(int j = 1; j<split.length; j++){
								if (!split[j].equals("")){
									structTypes.add(split[j]);
								}
							}
							
							
						}
					}
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for(String type: fgTypes){
				
					rules.add(new FunctionRule(type,"}",function,(char)0, true));
				}
				for(Object type:structTypes.toArray()){
					rules.add(new FunctionRule((String)type,"}",function,(char)0, true));
				}
				rules.add(new StructRule(struct)); 
				
				IPredicateRule[] result= new IPredicateRule[rules.size()];
				rules.toArray(result);
				setPredicateRules(result);
				
	}

}
