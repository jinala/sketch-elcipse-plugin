package sketchPlugin.editors;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class SketchContentOutlinePage extends ContentOutlinePage {

	/**
	 * A segment element.
	 */
	protected static class Segment {
		public String name;
		public Position position;

		public Segment(String name, Position position) {
			this.name= name;
			this.position= position;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Divides the editor's document into function segments
	 */
	protected class ContentProvider implements ITreeContentProvider {

		protected final static String SEGMENTS= "__sketch_segments"; //$NON-NLS-1$
		protected IPositionUpdater fPositionUpdater= new DefaultPositionUpdater(SEGMENTS);
		protected List fContent= new ArrayList();

		protected void parse(IDocument document) throws BadLocationException {
			int l = document.getLength();
			//System.out.println(l);

			for (ITypedRegion i : document.computePartitioning(0, l)) {

				try {
					if(i.getType().equals("__function")){
						int offset= i.getOffset();
						int length= i.getLength();		
						String function = document.get(offset, length);
						String name = getName(function);
						Position p= new Position(offset, length);
						document.addPosition(SEGMENTS, p);
						fContent.add(new Segment("Function: "+name, p)); 
					}else if (i.getType().equals("__struct")){
						int offset = i.getOffset();
						int length = i.getLength();
						String struct = document.get(offset,length);
						String name = getName(struct);
						Position p = new Position(offset, length);
						document.addPosition(SEGMENTS,p);
						fContent.add(new Segment("Struct: "+name,p));
					}

				} catch (BadPositionCategoryException x) {
				} catch (BadLocationException x) {
				}
			}
		}

		private String getName(String function) {

			String[]  split = function.split("[ (){}\n\t\r]");
			if(split[0].equals("harness")||split[0].equals("generator")||split[0].equals("stencil")){

				for(int i =1; i<split.length;i++){
					if(!split[i].equals("")){
						for(int j = i+1;j<split.length;j++){
							if(!split[j].equals("")){
								return split[j];
							}
						}
						return "";
					}
				}
			}
			for(int i = 1; i<split.length; i++){
				if (!split[i].equals("")){
					return split[i];
				}
			}
			return "";

		}

		/*
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (oldInput != null) {
				IDocument document= fDocumentProvider.getDocument(oldInput);
				if (document != null) {
					try {
						document.removePositionCategory(SEGMENTS);
					} catch (BadPositionCategoryException x) {
					}
					document.removePositionUpdater(fPositionUpdater);
				}
			}

			fContent.clear();

			if (newInput != null) {
				IDocument document= fDocumentProvider.getDocument(newInput);
				if (document != null) {
					document.addPositionCategory(SEGMENTS);
					document.addPositionUpdater(fPositionUpdater);

					try {
						parse(document);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		/*
		 * @see IContentProvider#dispose
		 */
		public void dispose() {
			if (fContent != null) {
				fContent.clear();
				fContent= null;
			}
		}

		/*
		 * @see IStructuredContentProvider#getElements(Object)
		 */
		public Object[] getElements(Object element) {
			return fContent.toArray();
		}

		/*
		 * @see ITreeContentProvider#hasChildren(Object)
		 */
		public boolean hasChildren(Object element) {
			return element == fInput;
		}

		/*
		 * @see ITreeContentProvider#getParent(Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof Segment)
				return fInput;
			return null;
		}

		/*
		 * @see ITreeContentProvider#getChildren(Object)
		 */
		public Object[] getChildren(Object element) {
			if (element == fInput)
				return fContent.toArray();
			return new Object[0];
		}
	}

	protected Object fInput;
	protected IDocumentProvider fDocumentProvider;
	protected ITextEditor fTextEditor;

	/**
	 * Creates a content outline page using the given provider and the given editor.
	 *
	 * @param provider the document provider
	 * @param editor the editor
	 */
	public SketchContentOutlinePage(IDocumentProvider provider, ITextEditor editor) {
		super();
		fDocumentProvider= provider;
		fTextEditor= editor;
	}

	/* (non-Javadoc)
	 * Method declared on ContentOutlinePage
	 */
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.addSelectionChangedListener(this);

		if (fInput != null)
			viewer.setInput(fInput);
	}

	/* (non-Javadoc)
	 * Method declared on ContentOutlinePage
	 */
	public void selectionChanged(SelectionChangedEvent event) {

		super.selectionChanged(event);

		ISelection selection= event.getSelection();
		if (selection.isEmpty())
			fTextEditor.resetHighlightRange();
		else {
			Segment segment= (Segment) ((IStructuredSelection) selection).getFirstElement();
			int start= segment.position.getOffset();
			int length= segment.position.getLength();
			try {
				fTextEditor.setHighlightRange(start, length, true);
			} catch (IllegalArgumentException x) {
				fTextEditor.resetHighlightRange();
			}
		}
	}

	/**
	 * Sets the input of the outline page
	 *
	 * @param input the input of this outline page
	 */
	public void setInput(Object input) {
		fInput= input;
		update();
	}

	/**
	 * Updates the outline page.
	 */
	public void update() {
		TreeViewer viewer= getTreeViewer();

		if (viewer != null) {
			Control control= viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				viewer.setInput(fInput);
				viewer.expandAll();
				control.setRedraw(true);
			}
		}
	}
}
