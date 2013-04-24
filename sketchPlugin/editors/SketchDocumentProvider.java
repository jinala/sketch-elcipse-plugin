package sketchPlugin.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;



public class SketchDocumentProvider extends FileDocumentProvider {
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		IDocumentExtension3 docExtension = (IDocumentExtension3) document;
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new SketchPartitionScanner(document),
					SketchPartitionScanner.SKETCH_PARTITION_TYPES);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
			for(ITypedRegion i : partitioner.computePartitioning(0, 1000)){
				if(i.getType().equals("__function")){
					
				}
			}
			
			
		}
		return document;
	}
}
