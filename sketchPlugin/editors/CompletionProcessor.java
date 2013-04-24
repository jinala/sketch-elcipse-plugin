package sketchPlugin.editors;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.internal.jobs.JobMessages;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class CompletionProcessor implements IContentAssistProcessor {
	private final IContextInformation[] NO_CONTEXTS ={};
	private final char[] PROPOSAL_ACTIVATION_CHARS ={'.','('};
	private ICompletionProposal[] NO_COMPLETIONS ={};
	private String[] fgProposals={"void"};

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		ICompletionProposal[] result = new ICompletionProposal[fgProposals.length];
		for(int i=0; i<fgProposals.length;i++){
			IContextInformation info = new ContextInformation(fgProposals[i],"");
			result[i] = new CompletionProposal(fgProposals[i],offset,0,fgProposals[i].length(),null,fgProposals[i],info,"");
			
		}
		return result;
			

	}
	/**
	 * 
	 * */

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		IContextInformation[] result = new IContextInformation[5];
		for(int i =0; i<result.length;i++)
			result[i] = new ContextInformation("","");
		return result;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return PROPOSAL_ACTIVATION_CHARS;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] {'#'};
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
