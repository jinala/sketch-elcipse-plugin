package sketchPlugin.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;


import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class SketchTextEditor extends TextEditor {
	private SketchContentOutlinePage fOutlinePage;
	/** The projection support */
	private ProjectionSupport fProjectionSupport;
	private ColorManager colorManager;
	public SketchTextEditor(){
		super();	
		colorManager = new ColorManager();
		SourceViewerConfiguration sv = new SketchSourceViewerConfiguration(colorManager);
		setSourceViewerConfiguration(sv);
		setDocumentProvider(new SketchDocumentProvider());
	}
	
	
	

	/** The <code>JavaEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs any extra
	 * disposal actions required by the java editor.
	 */
	public void dispose() {
		if (fOutlinePage != null)
			fOutlinePage.setInput(null);
		super.dispose();
	}

	/** The <code>JavaEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs any extra
	 * revert behavior required by the java editor.
	 */
	public void doRevertToSaved() {
		super.doRevertToSaved();
		if (fOutlinePage != null)
			fOutlinePage.update();
	}

	/** The <code>JavaEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs any extra
	 * save behavior required by the java editor.
	 *
	 * @param monitor the progress monitor
	 */
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		if (fOutlinePage != null)
			fOutlinePage.update();
	}

	/** The <code>JavaEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs any extra
	 * save as behavior required by the java editor.
	 */
	public void doSaveAs() {
		super.doSaveAs();
		if (fOutlinePage != null)
			fOutlinePage.update();
	}

	/** The <code>JavaEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs sets the
	 * input of the outline page after AbstractTextEditor has set input.
	 *
	 * @param input the editor input
	 * @throws CoreException in case the input can not be set
	 */
	public void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		if (fOutlinePage != null)
			fOutlinePage.setInput(input);
	}

	/*
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		addAction(menu, "ContentAssistProposal"); //$NON-NLS-1$
		addAction(menu, "ContentAssistTip"); //$NON-NLS-1$
		addAction(menu, "DefineFoldingRegion");  //$NON-NLS-1$
	}

	

	/*
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		fAnnotationAccess= createAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());

		ISourceViewer viewer= new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}

	/*
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionViewer viewer= (ProjectionViewer) getSourceViewer();
		fProjectionSupport= new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
		fProjectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#adjustHighlightRange(int, int)
	 */
	protected void adjustHighlightRange(int offset, int length) {
		ISourceViewer viewer= getSourceViewer();
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension= (ITextViewerExtension5) viewer;
			extension.exposeModelRange(new Region(offset, length));
		}
	}
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage= new SketchContentOutlinePage(getDocumentProvider(), this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getEditorInput());
			}
			return fOutlinePage;
		}

		if (fProjectionSupport != null) {
			Object adapter= fProjectionSupport.getAdapter(getSourceViewer(), required);
			if (adapter != null)
				return adapter;
		}

		return super.getAdapter(required);
	}
	
}
