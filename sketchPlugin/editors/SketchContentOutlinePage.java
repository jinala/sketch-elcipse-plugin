package sketchPlugin.editors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;
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

import antlr.RecognitionException;
import antlr.TokenStream;
import antlr.TokenStreamException;

import sketch.compiler.ast.core.Function;
import sketch.compiler.parser.StreamItLex;
import sketch.compiler.parser.StreamItParserFE;


public class SketchContentOutlinePage extends ContentOutlinePage {

	

	/**
	 * Divides the editor's document into function segments
	 */
	protected class ContentProvider implements ITreeContentProvider {

		protected final static String SEGMENTS= "__sketch_segments"; //$NON-NLS-1$
		protected IPositionUpdater fPositionUpdater= new DefaultPositionUpdater(SEGMENTS);
		protected List fContent= new ArrayList();

		protected void parse(IDocument document) throws BadLocationException, UnsupportedEncodingException {
			IDocumentPartitioner partitioner =
					new FastPartitioner(
						new SketchPartitionScanner(document),
						SketchPartitionScanner.SKETCH_PARTITION_TYPES);
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);
			int l = document.getLength();
		

			for (ITypedRegion i : document.computePartitioning(0, l)) {
				//System.out.println(i);
				try {
					if(i.getType().equals("__function")){
						int offset= i.getOffset();
						int length= i.getLength();		
						String function = document.get(offset, length);
						String name = getName(function);
						
						Segment f = getFuncTree(function,offset,length,document);
						
						Position p= new Position(offset, length);
						document.addPosition(SEGMENTS, p);
						fContent.add(f); 
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
				} catch (RecognitionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TokenStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		private Segment getFuncTree(String function, int offset,int length,IDocument document) throws UnsupportedEncodingException, RecognitionException, TokenStreamException{
			
			//System.out.println("function to parse: [" + function + "]");
			Reader r = new StringReader(function);
			
			StreamItLex lex = new StreamItLex(r);
			StreamItParserFE parser = new StreamItParserFE(lex, null, false, null);
			parser.setFilename("test");
			Function f = parser.function_decl();
			TreeBuilder func = new TreeBuilder(document,offset,length);
			return func.visitFunction(f,null);
			
			
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
			List temp = new ArrayList();
			temp.addAll(fContent);
			fContent.clear();

			if (newInput != null) {
				//System.out.println(newInput);
				IDocument document= fDocumentProvider.getDocument(newInput);
				if (document != null) {
					document.addPositionCategory(SEGMENTS);
					document.addPositionUpdater(fPositionUpdater);

					try {
						parse(document);
					} catch(java.lang.NullPointerException e){
						fContent.clear();
						fContent.addAll(temp);
						e.printStackTrace();
					
					}
					catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
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
			if(element == fInput){
				return true;
			}else if(element instanceof Segment){
				return ((Segment)element).getChildren().size()>0;
			}else{
				return false;
			}
		}

		/*
		 * @see ITreeContentProvider#getParent(Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof Segment){
				if(((Segment)element).getParent()!=null){
					return ((Segment)element).getParent();
				}else{
					return fInput;
				}
			}
			return null;
		}

		/*
		 * @see ITreeContentProvider#getChildren(Object)
		 */
		public Object[] getChildren(Object element) {
			if (element == fInput)
				return fContent.toArray();
			else if(element instanceof Segment){
				return ((Segment)element).getChildren().toArray();
			}
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

	
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.addSelectionChangedListener(this);

		if (fInput != null)
			viewer.setInput(fInput);
	}

	
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
				
				control.setRedraw(true);
			}
		}
	}
}
