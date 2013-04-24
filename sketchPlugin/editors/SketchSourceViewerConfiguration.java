package sketchPlugin.editors;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;

import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;



import sketchPlugin.editors.SketchCodeScanner.TextWords;

public class SketchSourceViewerConfiguration extends SourceViewerConfiguration {
	
	private ColorManager colorManager;
	private SketchCodeScanner scanner;
	private StructScanner structScanner;
	
	/**
	 * Single token scanner.
	 */
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
			
		}
	}
	static class SingletokenScanner1 extends RuleBasedScanner {
		public SingletokenScanner1(ColorManager manager){
			IToken defaultToken = new Token(new TextAttribute(manager.getColor(IColorConstants.DEFAULT),null,SWT.ITALIC));
			IToken intToken = new Token(new TextAttribute(manager.getColor(IColorConstants.TYPE),null,SWT.BOLD));
			WordRule wordRule = new WordRule(new TextWords(),defaultToken);
			wordRule.addWord("int", intToken);
			IRule[] rule = new IRule[1];
			rule[0]= wordRule;
		}
		
	}
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
			return new IAutoEditStrategy[] { new SketchAutoIndent() };
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return new SketchDoubleClickSelector();
	}
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "\t", "    " }; 
	}
	public SketchSourceViewerConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		String[] types = new String[SketchPartitionScanner.SKETCH_PARTITION_TYPES.length+1];
		types[0] = IDocument.DEFAULT_CONTENT_TYPE;
		for(int i =1; i<SketchPartitionScanner.SKETCH_PARTITION_TYPES.length+1;i++){
			types[i] = SketchPartitionScanner.SKETCH_PARTITION_TYPES[i-1];
		}
		
		return types;
	}
	protected SketchCodeScanner getScanner(){
		if(scanner==null){
			scanner = new SketchCodeScanner(colorManager);
		}
		return scanner;
	}
	protected StructScanner getStructScanner(){
		if(structScanner==null){
			structScanner= new StructScanner(colorManager);
			
		}
		return structScanner;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler pr = new PresentationReconciler();
		//pr.setDocumentPartitioning("__sketch_partitioning");
		
		DefaultDamagerRepairer dr;
		
		//System.out.println(getStructScanner().getTokenLength());
			
		
		dr = new DefaultDamagerRepairer(new FunctionScanner(colorManager));
		pr.setDamager(dr,SketchPartitionScanner.FUNCTION);
		pr.setRepairer(dr,SketchPartitionScanner.FUNCTION);
		
		dr = new DefaultDamagerRepairer(new SingletokenScanner1(colorManager));
		pr.setDamager(dr,"_Optional_token");
		pr.setRepairer(dr,"_Optional_token");
	
		
		dr = new DefaultDamagerRepairer(getStructScanner());
		pr.setDamager(dr, SketchPartitionScanner.STRUCT);
		pr.setRepairer(dr, SketchPartitionScanner.STRUCT);
		
		
		dr = new DefaultDamagerRepairer(getScanner());
		pr.setDamager(dr,IDocument.DEFAULT_CONTENT_TYPE);
		pr.setRepairer(dr,IDocument.DEFAULT_CONTENT_TYPE);
		
		
		NonRuleBasedDamagerRepairer ndr =
				new NonRuleBasedDamagerRepairer(
					new TextAttribute(
						colorManager.getColor(IColorConstants.MULTICOMMENT)));
			pr.setDamager(ndr, SketchPartitionScanner.COMMENT);
			pr.setRepairer(ndr, SketchPartitionScanner.COMMENT);
		
		
		
		return pr;
	}
	
}
